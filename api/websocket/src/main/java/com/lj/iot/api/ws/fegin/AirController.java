package com.lj.iot.api.ws.fegin;

import com.alibaba.fastjson.JSON;
import com.lj.iot.api.ws.WS;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.common.base.vo.CommonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/open/socket")
@RestController
public class AirController {


    @RequestMapping("/appSend")
    public CommonResultVo<String> appSend( @RequestBody  WsResultVo wsResultVo) {
        List<String> userList = wsResultVo.getUserIds();
        wsResultVo.setUserIds(null);
        WS.appSend(userList, JSON.toJSONString(wsResultVo));
        return CommonResultVo.SUCCESS();
    }

    @RequestMapping("/hotelSend")
    public CommonResultVo<String> hotelSend(@RequestBody  WsResultVo wsResultVo) {
        List<String> userList = wsResultVo.getUserIds();
        wsResultVo.setUserIds(null);
        WS.hotelSend(userList, JSON.toJSONString(wsResultVo));
        return CommonResultVo.SUCCESS();
    }
}
