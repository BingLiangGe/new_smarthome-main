package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSceneVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 家ID
     */
    private Long homeId;

    /**
     * 场景ID
     */
    private Long  sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 家用户ID
     */
    private Long homeUserId;

    /**
     * 管理员用户(授权)
     */
    private String userId;

    /**
     * 是否授权，0未授权，1已授权
     */
    private int status;

}
