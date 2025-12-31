package com.lj.iot.biz.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SetHomeUserDeviceAuthorityDto  {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;


    private long homeUserId;

    private long homeId;

    /**
     * 用户设备/场景授权列表
     */
    private List<UserDeviceAuth> userDeviceAuths;

    private Integer authType;

    @Data
    public class UserDeviceAuth {

        /**
         * @Fields serialVersionUID :
         */
        private static final long serialVersionUID = 1L;

        /**
         * 用户设备Id/场景ID
         */
        private long deviceOrSceneId;
        /**
         *  权限;1:授权;2:取消授权
         */
        private int authority = 1;
    }
}
