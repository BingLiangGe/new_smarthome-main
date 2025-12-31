package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeUserJoinPageDto extends PageDto {

    /**
     * 家庭ID
     */
    private Long homeId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 成员ID
     */
    private String memberUserId;

    /**
     * 动作
     */
    private String action;

    /**
     * 状态
     */
    private String state;
}
