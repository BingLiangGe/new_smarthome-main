package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 意图查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentConfigPageDto extends PageDto {

    /**
     * 意图名称
     */
    private String intentName;

}
