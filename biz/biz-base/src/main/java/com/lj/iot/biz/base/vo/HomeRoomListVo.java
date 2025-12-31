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
public class HomeRoomListVo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 房间
     */
    private Long id;

    /**
     * 房间名
     */
    private String roomName;
}
