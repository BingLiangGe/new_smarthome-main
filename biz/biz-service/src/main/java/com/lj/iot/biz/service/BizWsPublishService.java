package com.lj.iot.biz.service;

import com.lj.iot.biz.base.vo.WsResultVo;

/**
 *
 */
public interface BizWsPublishService {

    void publish(WsResultVo wsResultVo);

    void publishAllMemeberFailure(String topic,String msg);

    void publishAllMemberByHomeId(String topic, Long homeId, Object data);

    void publishEditMemberByHomeId(String topic, Long homeId, Object data);

    void publishAllMemberByHomeIdFailure(String topic, Long homeId, Object data);

    void publishEditMemberByHomeIdFailure(String topic, Long homeId, Object data);

    void publishEditMemberByHomeIdFailure(String topic, Long homeId, Object data,String msg);

    void publishDevice(String topic, Object data);

}
