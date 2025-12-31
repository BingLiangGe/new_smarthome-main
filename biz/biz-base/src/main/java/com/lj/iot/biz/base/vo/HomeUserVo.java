package com.lj.iot.biz.base.vo;

import com.sun.source.doctree.SerialDataTree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeUserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 家用户ID
     */
    private Long homeUserId;

    private Long homeId;

    /**
     * 用户昵称
     */
    private String nickName;


    private Boolean isMain;
}
