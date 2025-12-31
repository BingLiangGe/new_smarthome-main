package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRfKeyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 码值（整型数组）
     */
    private String codeData;

    /**
     * 是否有效(1:是;0:否),默认否
     */
    private Boolean isEffective=false;

    /**
     * 设备型号按键Id
     */
    @NotNull(message = "设备型号按键Id不能为空")
    private Long keyId;

    /**
     * 设备型号Id
     */
    @NotNull(message = "设备型号Id不能为空")
    private Long modelId;

    /**
     * 用户设备主键Id
     */
    @NotNull(message = "用户设备主键Id不能为空")
    private String userDeviceId;

//    /**
//     * 用户Id
//     */
//    @NotNull(message = "用户Id不能为空")
//    private String userId;
}
