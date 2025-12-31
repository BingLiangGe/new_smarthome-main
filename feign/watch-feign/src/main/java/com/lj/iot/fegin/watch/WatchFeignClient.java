package com.lj.iot.fegin.watch;

import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "watch")
public interface WatchFeignClient {

    /**
     * 统一topic
     *
     * @return
     */
    @RequestMapping(value = "inner/watch/app/topic",consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> watchSendMsg(@RequestBody WatchMsgDto watchMsgDto);
}
