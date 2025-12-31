package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 楼层ID
     */
    private Long floorId;

    /**
     * 楼层名称
     */
    private String floorName;

    private Long outLine;


    private List<FloorHomeVo> list;
}
