package com.lj.iot.fegin.websocket;

import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "ws")
public interface WsFeignClient {

    /**
     * 统一topic
     *
     * @param wsResultVo
     * @return
     */
    @RequestMapping(value = "inner/ws/app/topic", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> appSend(@RequestBody WsResultVo wsResultVo);

    @RequestMapping(value = "inner/ws/hotel/topic", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> hotelSend(@RequestBody WsResultVo wsResultVo);
}
