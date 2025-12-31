package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    private String text;
    private String type = "T";

    public static Answer MSG(String msg) {
        Answer answer = new Answer();
        answer.setText(msg);
        return answer;
    }
}
