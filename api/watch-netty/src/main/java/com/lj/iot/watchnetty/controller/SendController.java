package com.lj.iot.watchnetty.controller;


import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.watchnetty.server.BootNettyChannelInboundHandlerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/open/send")
@RestController
public class SendController {

    private static final String DEVICE_ID = "358800001618876";

    @RequestMapping("/sendData")
    public CommonResultVo<String> sendData(String deviceId,String msg) {
        BootNettyChannelInboundHandlerAdapter.sendMsg(deviceId,"["+ msg+"]");
        return CommonResultVo.SUCCESS();
    }
}
