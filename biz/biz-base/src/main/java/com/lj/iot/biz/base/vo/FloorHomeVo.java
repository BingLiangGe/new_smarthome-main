package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorHomeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 楼层ID
     */
    private Long floorId;

    /**
     * 家庭ID
     */
    private Long homeId;

    /**
     * 家庭名称
     */
    private String homeName;

    private Long outLine;
}
