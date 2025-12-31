package com.lj.iot.api.system.web.auth;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceBachCodeDto;
import com.lj.iot.biz.base.dto.DeviceExcelImportDto;
import com.lj.iot.biz.base.dto.DeviceExportDto;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.base.vo.DeviceStatisticsVo;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 设备管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/device")
public class AuthDeviceController {

    @Autowired
    private BizDeviceService bizDeviceService;




    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("device:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<DevicePageVo>> devicePage(PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizDeviceService.customPage(pageDto),
                bizDeviceService.statistics(pageDto));
    }


    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("device:page")
    @RequestMapping("/newPage")
    public CommonResultVo<IPage<DevicePageVo>> newPage(OtaDeviceIdPageDto pageDto) {
        IPage<DevicePageVo> devicePageVoIPage = bizDeviceService.newCustomPage(pageDto);
        DeviceStatisticsVo deviceStatisticsVo = bizDeviceService.newStatistics(pageDto);

        return CommonResultVo.SUCCESS(devicePageVoIPage,
                deviceStatisticsVo);
    }

    /**
     * 根据批次号导出mqtt秘钥
     *
     * @param response
     * @return
     */
    @CustomPermissions("device:export_json")
    @RequestMapping("/export_json")
    public CommonResultVo<String> exportJson(@Valid DeviceBachCodeDto dto, HttpServletResponse response) {
        bizDeviceService.exportJson(dto, response);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 生成并导出列表
     *
     * @param response
     * @return
     */
    @CustomPermissions("device:export")
    @RequestMapping("/export")
    public CommonResultVo<String> export(@Valid DeviceExportDto dto, HttpServletResponse response) throws IOException {
        bizDeviceService.exportExcel(dto, response);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 根据批次号导出列表
     *
     * @param response
     * @return
     */
    @CustomPermissions("device:export_batch")
    @RequestMapping("/export_batch")
    public CommonResultVo<String> exportByBatchCode(@Valid DeviceBachCodeDto dto, HttpServletResponse response) {
        bizDeviceService.exportByBatchCode(dto, response);
        return CommonResultVo.SUCCESS();
    }
}
