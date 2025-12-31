package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 酒店用户与角色对应关系
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hotel_user_role")
public class HotelUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long hotelId;

    /**
     * 用户ID
     */
    private String hotelUserId;

    /**
     * 角色ID
     */
    private Long roleId;
}
