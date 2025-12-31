package com.lj.iot.feign.hotel;

import com.lj.iot.common.base.dto.LockMasterDeviceDto;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.SendVoiceDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "hotel-api")
public interface HotelApiFeignClient {

    /**
     * 发送数据
     * @param dto 消息控制实体
     * @return
     */
    @PostMapping(value = "inner/user_device/send_data", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> sendData(@RequestBody @Valid SendDataDto dto) throws InterruptedException;

     /**
     *
     * @param dto
     */
     @PostMapping(value = "inner/user_device/send_voice", consumes = MediaType.APPLICATION_JSON_VALUE)
     CommonResultVo<String> sendVoice(@RequestBody @Valid SendVoiceDto dto) throws InterruptedException;

    /**
     *
     * @param dto
     * @return
     */
     @PostMapping(value = "inner/user_device/lock_master_device", consumes = MediaType.APPLICATION_JSON_VALUE)
     CommonResultVo<String> lockMasterDevice(@RequestBody @Valid LockMasterDeviceDto dto) throws InterruptedException;
}
