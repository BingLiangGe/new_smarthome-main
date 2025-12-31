package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 锁主控
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LockMasterDeviceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * homeId
     */
    @NotNull(message = "homeId不能为空")
    private Long homeId;

    /**
     * 类型 1锁定 0解锁
     */
    @NotNull(message = "操作类型不能为空")
    private Integer type;
}
