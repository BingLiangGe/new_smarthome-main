package com.lj.iot.common.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    //平台
    private String platform;
    private String uId;//ID
    private String account;//账号

    //酒店账号actualUserId和 user_account的actualUserId相同，且酒店账号的主账号ID和user_account主账号ID相同，具体可以看酒店登录注册逻辑
    private String actualUserId;

    //酒店专用
    private Boolean isMain;
    private Long hotelId;

    //酒店和管理后台用
    private List<String> perms;

    private String openId;
/*
    private Long tenantId;
    private String roleCode;*/

    public static UserDto getUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (UserDto) request.getAttribute("currentUser");
    }

    @JsonIgnore
    public Long getLongId() {
        return Long.valueOf(this.uId);
    }
}
