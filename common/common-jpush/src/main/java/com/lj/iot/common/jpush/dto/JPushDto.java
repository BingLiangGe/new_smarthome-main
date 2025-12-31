package com.lj.iot.common.jpush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JPushDto {

    /**
     * 号码
     */
    private List<String> alias;
    private Alert alert;


}
