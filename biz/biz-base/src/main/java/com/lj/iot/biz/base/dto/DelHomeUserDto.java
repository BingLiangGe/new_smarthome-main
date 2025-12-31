package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DelHomeUserDto implements Serializable {
    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;

    /**
     * 家用户ID
     */
    @NotNull(message = "家用户ID不能为空")
    private String homeUserId;

    /**
     * 家ID
     */
    @NotNull(message = "家ID不能为空")
    private long homeId;
}
