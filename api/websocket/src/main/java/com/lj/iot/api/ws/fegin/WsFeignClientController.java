package com.lj.iot.api.ws.fegin;

import com.alibaba.fastjson.JSON;
import com.lj.iot.api.ws.WS;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class WsFeignClientController implements WsFeignClient {

    @Override
    public CommonResultVo<String> appSend(WsResultVo wsResultVo) {
        List<String> userList = wsResultVo.getUserIds();
        wsResultVo.setUserIds(null);
        WS.appSend(userList, JSON.toJSONString(wsResultVo));
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> hotelSend(WsResultVo wsResultVo) {
        List<String> userList = wsResultVo.getUserIds();
        wsResultVo.setUserIds(null);
        WS.hotelSend(userList, JSON.toJSONString(wsResultVo));
        return CommonResultVo.SUCCESS();
    }
}
