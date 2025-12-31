package com.lj.iot.common.aiui.core.service;

import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.vo.IntentResult;

/**
 * 识别文本
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
public interface IIdentifyingTextPostProcessor {

    void handle(IntentDto intentDto);
}
