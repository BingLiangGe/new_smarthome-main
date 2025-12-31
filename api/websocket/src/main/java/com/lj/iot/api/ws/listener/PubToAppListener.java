/*
package com.lj.iot.api.ws.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.api.ws.WS;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.redis.anno.RedisComponent;
import com.lj.iot.common.redis.anno.RedisListener;

*/
/**
 * @author mz
 * @Date 2022/8/3
 * @since 1.0.0
 *//*

@RedisComponent
public class PubToAppListener {

    */
/**
     * 推送消息设备删除消息至APP
     *//*

    @RedisListener(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_DELETE)
    public void handlerDeviceDelete(String msg) {
        JSONObject jsonObject = JSON.parseObject(msg);
        jsonObject.put("channel", "TOPIC_CHANNEL_DEVICE_DELETE");
        WS.send(JSONObject.parseArray(jsonObject.getString("userId"), String.class), JSON.toJSONString(CommonResultVo.SUCCESS(jsonObject)));
    }

    */
/**
     * 推送消息设备删除消息至APP
     *//*

    @RedisListener(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD)
    public void handlerDeviceAdd(String msg) {
        JSONObject jsonObject = JSON.parseObject(msg);
        jsonObject.put("channel", "TOPIC_CHANNEL_DEVICE_ADD");
        WS.send(JSONObject.parseArray(jsonObject.getString("userId"), String.class), JSON.toJSONString(CommonResultVo.SUCCESS(jsonObject)));
    }

    @RedisListener(RedisTopicConstant.TOPIC_CHANNEL_PUBLIC_TOPIC)
    public void publicTopic(String msg) {
        JSONObject jsonObject = JSON.parseObject(msg);
        String userIdStr = jsonObject.getString("userId");
        jsonObject.remove("userId");
        if (userIdStr.contains("[")) {
            WS.send(JSONObject.parseArray(userIdStr, String.class), JSON.toJSONString(jsonObject));
            return;
        }
        WS.send(userIdStr, JSON.toJSONString(jsonObject));
    }
}
*/
