package com.lj.iot.watchnetty.feign;

import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.fegin.watch.WatchFeignClient;
import com.lj.iot.watchnetty.server.BootNettyChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class WatchFeignClientController implements WatchFeignClient {

    @Override
    public CommonResultVo<String> watchSendMsg(WatchMsgDto watchMsgDto) {
        BootNettyChannelInboundHandlerAdapter.sendMsg(watchMsgDto.getDeviceId(), "[" + watchMsgDto.getData() + "]");
        return CommonResultVo.SUCCESS();
    }
}
