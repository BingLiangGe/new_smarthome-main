package com.lj.iot.common.aiui.core.service;

import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.vo.AIUIResultVo;
import com.lj.iot.common.aiui.core.vo.IntentResult;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
public interface ISkillPostProcessor {

    void handle(IntentDto intentDto);
}
