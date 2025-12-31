package com.lj.iot.common.aiui.core.service;

import com.alibaba.fastjson.JSONObject;

/**
 * ws上传语音文件返回
 *
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
public interface ISkillWsAckProcessor {

    void handle(JSONObject data);
}
