package com.lj.iot.api.hotel.web.feign;

import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.base.dto.LockMasterDeviceDto;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.SendVoiceDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.feign.hotel.HotelApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 内部接口，控制硬件使用
 */
@Slf4j
@RestController
public class InnerHotelController implements HotelApiFeignClient {
    @Resource
    private BizUserDeviceService bizUserDeviceService;

    private static final String SUCCESS = "success";
    @Override
    public CommonResultVo<String> sendData(@Valid SendDataDto dto) throws InterruptedException {
        Thread.sleep(1000L);
        log.error("------InnerHotelController-----sendData----SendDataDto={}", JSONObject.toJSONString(dto));
        bizUserDeviceService.sendData(dto, OperationEnum.APP_C);
        return CommonResultVo.SUCCESS(SUCCESS);
    }

    @Override
    public CommonResultVo<String> sendVoice(@Valid SendVoiceDto dto) throws InterruptedException {
        log.error("------InnerHotelController--------sendVoiceAnnouncement----SendVoiceDto={}", JSONObject.toJSONString(dto));
        Thread.sleep(10000L);
        bizUserDeviceService.sendVoice(dto);
        return CommonResultVo.SUCCESS(SUCCESS);
    }

    @Override
    public CommonResultVo<String> lockMasterDevice(@Valid LockMasterDeviceDto dto) throws InterruptedException {
        Thread.sleep(1000L);
        log.error("------InnerHotelController--------lockMasterDevice----LockMasterDeviceDto={}", JSONObject.toJSONString(dto));
        bizUserDeviceService.lockMasterDevice(dto);
        return CommonResultVo.SUCCESS(SUCCESS);
    }
}