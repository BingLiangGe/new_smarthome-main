package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.ActivationVo;
import com.lj.iot.biz.base.vo.DeviceStatisticsVo;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * 设备出厂表 服务类
 *
 *
 * @author xm
 * @since 2022-07-20
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 激活3326
     * @param params
     * @return
     */
    CommonResultVo<ActivationVo>  activation3326(@RequestBody Map<String, Object> params) throws IOException;

    List<String> findNotBindDevice();

    List<String> findUserAcccountDeviceNotHotel(Integer productId);

    IPage<DevicePageVo> customPage(PageDto pageDto);

    IPage<DevicePageVo> NewCustomPage(OtaDeviceIdPageDto pageDto);

    DeviceStatisticsVo statistics(PageDto pageDto);

    DeviceStatisticsVo newStatistics(OtaDeviceIdPageDto pageDto);

    String sha256(String deviceId);
}
