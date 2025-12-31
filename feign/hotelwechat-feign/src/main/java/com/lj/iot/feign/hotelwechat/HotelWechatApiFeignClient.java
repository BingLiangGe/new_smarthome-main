package com.lj.iot.feign.hotelwechat;

import com.lj.iot.common.base.dto.SendVoiceDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "hotel-wechat-api")
public interface HotelWechatApiFeignClient {

    /**
     *
     * @param orderNo 订单no
     * @return
     */
    @PostMapping(value = "inner/hotel_order/cancel_order", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> cancelOrderByOrderNo(@RequestBody String orderNo);

    /**
     *
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/hotel_order/completed_order", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> completedOrderByOrderNo(@RequestBody SendVoiceDto sendVoiceDto);

    /**
     *
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/hotel_order/room_send_voice", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> roomSendVoice(@RequestBody SendVoiceDto sendVoiceDto);

    /**
     *
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/hotel_order/close_devices", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> closeDevices(@RequestBody SendVoiceDto sendVoiceDto);

    /**
     *
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/hotel_order/open_devices", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> openDevices(@RequestBody SendVoiceDto sendVoiceDto);

    /**
     *
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/hotel_order/open_continuation_orders_devices", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> openContinuationOrdersDevices(@RequestBody SendVoiceDto sendVoiceDto);

    /**
     * 生成房务job
     * @param sendVoiceDto
     * @return
     */
    @PostMapping(value = "inner/room_service/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> createRoomService(@RequestBody SendVoiceDto sendVoiceDto);
}
