package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneDataListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 场景名称
     */
    private String sceneName;


    /**
     * 启用状态 0 启用  1 禁用
     */
    private int enable;

    /**
     * 家iD
     */
    private Long homeId;

    /**
     * 唤醒语料 [{"text": "我回来了"}]
     */
    private List<String> params;
}
