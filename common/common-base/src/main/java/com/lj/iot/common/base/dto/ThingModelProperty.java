package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingModelProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String identifier;
    private Object value;
    private Integer sceneId;
    private ThingModelDataType dataType;
}

