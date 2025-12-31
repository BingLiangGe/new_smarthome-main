package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityEntryPageDto extends PageDto {

    private String intentName;

    private String entityKey;
}
