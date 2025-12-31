package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticePageDto extends PageDto {

    /**
     * 类型 1：商品、前台    2：SOS
     */
    private Integer type;

    private Integer homeId;
}
