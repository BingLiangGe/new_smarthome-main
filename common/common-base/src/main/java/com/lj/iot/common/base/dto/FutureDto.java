package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mz
 * @Date 2022/7/28
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FutureDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private Object body;
    private String code;
    private String message;
}
