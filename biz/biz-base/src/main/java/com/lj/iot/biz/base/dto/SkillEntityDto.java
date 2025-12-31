package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class SkillEntityDto {
    /**
     * 主键,自动生成
     */
    private Long id;

    /**
     * 实体标识;对应iflyos实体标识
     */
    @NotNull(message = "实体标识不能为空")
    private String entityFlag;

    /**
     * 实体名称;对应iflyos实体名称
     */
    @NotNull(message = "实体名称不能为空")
    private String entityName;
}
