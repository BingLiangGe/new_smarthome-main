package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadEntityDto {

    /**
     * 用户名称
     */
    private String userId;

    /**
     * 动态实体名称
     */
    private String dynamicEntitiesName;

    /**
     * 官方实体名
     */
    private String resName;

    /**
     * 实体列表
     */
    private List<UploadEntityItemDto> entityList;


    private String namespace;
}
