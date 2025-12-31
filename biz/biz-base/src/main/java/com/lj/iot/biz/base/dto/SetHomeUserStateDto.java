package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetHomeUserStateDto {
    private static final long serialVersionUID = 1L;

    public static final int TYPE_INVITE = 1;
    public static final int TYPE_APPLY = 2;
    /**
     * 家用户Id
     */
    @NotNull(message = "家用户Id不能为空")
    private String homeUserId;
    /**
     * 家Id
     */
    @NotNull(message = "家Id不能为空")
    private long homeId;
    /**
     * 审核结果;2:同意;3:拒绝
     */
    @NotNull(message = "审核结果")
    private int state;
    /**
     * 类型;1:管理员邀请,用户操作;2:用户申请,管理员审核
     */
    private int type = TYPE_INVITE;
}
