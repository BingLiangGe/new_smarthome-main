package com.lj.iot.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceBachCodeDto;
import com.lj.iot.biz.base.dto.DeviceExportDto;
import com.lj.iot.biz.base.dto.IdStrDto;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.base.vo.DeviceStatisticsVo;
import com.lj.iot.biz.base.vo.ExportDeviceJsonVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.DeviceIdUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mz
 * @Date 2022/7/20
 * @since 1.0.0
 */
@Slf4j
@Service
public class BizDeviceServiceImpl implements BizDeviceService {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IProductService productService;

    @Override
    public IPage<DevicePageVo> customPage(PageDto pageDto) {
        return deviceService.customPage(pageDto);
    }

    @Override
    public IPage<DevicePageVo> newCustomPage(OtaDeviceIdPageDto pageDto) {
        List<String> hotelDevice = deviceService.findUserAcccountDeviceNotHotel(213350486);

        if (!hotelDevice.isEmpty()) {
            pageDto.setHotelDevice(hotelDevice);
        } else {
            pageDto.setHotelDevice(null);
        }
        return deviceService.NewCustomPage(pageDto);
    }

    @Override
    public DeviceStatisticsVo statistics(PageDto pageDto) {
        return deviceService.statistics(pageDto);
    }

    @Override
    public DeviceStatisticsVo newStatistics(OtaDeviceIdPageDto pageDto) {
        return deviceService.newStatistics(pageDto);
    }

    @Override
    public void batchSave(IdStrDto idStrDto, MultipartFile file) {
        Product product = productService.getById(idStrDto.getId());
        ValidUtils.isNullThrow(product, "产品不存在");
        String macStr = fileToString(file);
        List<String> macList = Arrays.stream(macStr.split(",")).filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        int length = macList.size();
        if (length == 0) {
            return;
        }

        final int batchSize = 10;
        int round = (length - 1) / batchSize;

        for (int i = 0; i <= round; i++) {
            // 求每个批次起始位置
            int fromIndex = i * batchSize;
            int toIndex = (i + 1) * batchSize;
            // 如果是最后一个批次，则不能越界
            if (i == round) {
                toIndex = length;
            }

            List<Device> deviceList = macList.subList(fromIndex, toIndex).stream().map(
                    s -> Device.builder()
                            .batchCode(s)
                            .productId(idStrDto.getId())
                            .CCCFDF(IdUtil.fastSimpleUUID())
                            .build()
            ).collect(Collectors.toList());
            deviceService.saveBatch(deviceList);
        }
    }

    @Override
    public void exportJson(DeviceBachCodeDto dto, HttpServletResponse response) {
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode(dto.getBatchCode())
                .build()));
        List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                        .user_id(device.getId())
                        .password_hash(device.getCCCFDF())
                        .build())
                .collect(Collectors.toList());
        exportTxt(response, JSON.toJSONString(deviceJsonVoList));
    }

    @Value("${mqtt.client.host}")
    private String host;

    @DSTransactional
    @Override
    public void exportExcel(DeviceExportDto dto, HttpServletResponse response) throws IOException {

        Product product = productService.getById(dto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");

        final int batchSize = dto.getNumber();
        ValidUtils.isNullThrow(batchSize >= 1 && batchSize <= 10000, "产品不存在");

        String batchCode = IdUtils.nextId();
        List<Device> list = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            list.add(Device.builder()
                    .id(DeviceIdUtils.hexId())
                    .productId(dto.getProductId())
                    .CCCFDF(IdUtils.uuid())
                    .batchCode(batchCode)
                    .build());
        }
        deviceService.saveBatch(list);

        //生成文件并上传给EMQX*********//

        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode(batchCode)
                .build()));
        List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                        .user_id(device.getId())
                        .password_hash(device.getCCCFDF())
                        .build())
                .collect(Collectors.toList());

        //创建json文件
        //String path = "/data/service/system-api/mqtt/inemqx.json";


        if (dto.getProductId().equals("213350486")) {

            String path = "C:\\Users\\A\\Desktop\\inemqx.json";
            createTxt(response, JSON.toJSONString(deviceJsonVoList), path);
            //上传给emqx
            import_users(path);
        }

        log.info("host={}",host);
        if ("172.16.0.236".equals(host)) {
            exportExcelWithData(list, batchCode, response, "正式环境", product.getProductName());
        } else if ("47.100.211.192".equals(host)) {
            exportExcelWithData(list, batchCode, response, "酒店分离环境", product.getProductName());
        } else if ("emqxprod.lj-smarthome.com".equals(host)) {
            exportExcelWithData(list, batchCode, response, "棋牌室环境", product.getProductName());
        } else {
            exportExcelWithData(list, batchCode, response, "测试环境", product.getProductName());
        }
    }


    private void exportExcelWithData(List<Device> deviceList, String batchCode, HttpServletResponse response, String text, String productName) {
        try {

            deviceList.forEach(device -> {
                device.setHostName(text);
            });


            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(batchCode, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + text + productName + deviceList.size() + "个_" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), Device.class)
                    .sheet("三元组")
                    .doWrite(() -> {
                        return deviceList;
                    });
        } catch (Exception e) {

        }
    }

    private void exportExcel(List<Device> deviceList, String batchCode, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(batchCode, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), Device.class)
                    .sheet("三元组")
                    .doWrite(() -> {
                        return deviceList;
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public void exportByBatchCode(DeviceBachCodeDto dto, HttpServletResponse response) {
        List<Device> list = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode(dto.getBatchCode())
                .build()));
        exportExcel(list, dto.getBatchCode(), response);
    }

    @Override
    public Device findById(String deviceId) {
        return deviceService.getById(deviceId);
    }

    @Override
    public void upDataById(Device device) {
        deviceService.updateById(device);
    }

    /* 导出txt文件
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
    public void exportTxt(HttpServletResponse response, String text) {
        response.setCharacterEncoding("utf-8");
        //设置响应的内容类型
        response.setContentType("text/plain");
        //设置文件的名称和格式
        response.addHeader("Content-Disposition", "attachment;filename="
                + genAttachmentFileName("emqx秘钥", "JSON_FOR_UCC_")//设置名称格式，没有这个中文名称无法显示
                + ".json");
        BufferedOutputStream buff = null;
        ServletOutputStream outStr = null;
        try {
            outStr = response.getOutputStream();
            buff = new BufferedOutputStream(outStr);
            buff.write(text.getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (Exception e) {
            //LOGGER.error("导出文件文件出错:{}",e);
        } finally {
            try {
                buff.close();
                outStr.close();
            } catch (Exception e) {
                //LOGGER.error("关闭流对象出错 e:{}",e);
            }
        }
    }


    /* 生成txt文件
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
    public void createTxt(HttpServletResponse response, String text, String path) throws IOException {
        try {
            log.info("##############################文件创建开始################################");
            File destFile = new File(path);
            destFile.createNewFile();
            Writer write = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
            write.write(text);
            write.flush();
            write.close();
            log.info("##############################文件创建成功################################");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Value("${mqtt.client.host}")
    private String hostIp;


    /* 文件上传到emqx
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
    public String import_users(String path) throws IOException {

        String credential = null;
        if ("47.100.211.192".equals(host) || "emqxprod.lj-smarthome.com".equals(host)) {
            credential = Credentials.basic("admin", "pulicadmin");
        } else {
            credential = Credentials.basic("admin", "public");
        }
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("filename", path,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(path)))
                .build();
        Request request = new Request.Builder()
                .url("http://" + hostIp + ":18083/api/v5/authentication/password_based%3Abuilt_in_database/import_users")
                .method("POST", body)
                .header("Authorization", credential)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        return response.body().string();
    }


    public String genAttachmentFileName(String cnName, String defaultName) {
        try {
            cnName = new String(cnName.getBytes("gb2312"), "ISO8859-1");
        } catch (Exception e) {
            cnName = defaultName;
        }
        return cnName;
    }


    private String fileToString(MultipartFile file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = (FileInputStream) file.getInputStream();
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(100);

            StringBuilder stringBuilder = new StringBuilder();
            while (channel.read(buffer) != -1) {
                // 读取信息
                buffer.flip();
                while (buffer.hasRemaining()) {
                    stringBuilder.append((char) buffer.get());
                }
                buffer.clear();
            }
            return stringBuilder.toString().replaceAll(" ", "")
                    .replaceAll("(\\r\\n|\\n|\\n\\r)", ",");
        } catch (Exception e) {
            log.error("BizDeviceServiceImpl.fileToString", e);
            throw CommonException.FAILURE("处理文件异常");
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                log.error("BizDeviceServiceImpl.fileToString", e);
            }
        }
    }
}
