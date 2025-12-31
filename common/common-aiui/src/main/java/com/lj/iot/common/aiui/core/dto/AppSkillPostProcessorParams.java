package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSkillPostProcessorParams {

    private String MsgId;
    private Long CreateTime;
    private String AppId;
    private String UserId;
    private String SessionParams;
    private String UserParams;
    private String FromSub;
    private Msg Msg;
}
