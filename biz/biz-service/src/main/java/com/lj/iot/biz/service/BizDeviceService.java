package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceBachCodeDto;
import com.lj.iot.biz.base.dto.DeviceExportDto;
import com.lj.iot.biz.base.dto.IdStrDto;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.DeviceStatisticsVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 设备表
 */
public interface BizDeviceService {

    IPage<DevicePageVo> customPage(PageDto pageDto);

    IPage<DevicePageVo> newCustomPage(OtaDeviceIdPageDto pageDto);

    /**
     * 统计激活
     * @param pageDto
     * @return
     */
    DeviceStatisticsVo statistics(PageDto pageDto);

    DeviceStatisticsVo newStatistics(OtaDeviceIdPageDto pageDto);

    void batchSave(IdStrDto idStrDto, MultipartFile file);

    void exportJson(DeviceBachCodeDto dto,HttpServletResponse response);

    /**
     * 导出设备
     *
     * @param response
     */
    void exportExcel(DeviceExportDto dto, HttpServletResponse response) throws IOException;

    void exportByBatchCode(DeviceBachCodeDto dto, HttpServletResponse response);

    Device findById(String deviceId);

    void upDataById(Device device);
}
