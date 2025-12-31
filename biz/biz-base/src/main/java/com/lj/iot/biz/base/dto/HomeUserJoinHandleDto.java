package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class HomeUserJoinHandleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 状态  true同意  false拒绝
     */
    @NotNull(message = "状态不能为空")
    private Boolean flag;

    /**
     * ID
     */
    private Long homeId;
}
