package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * 紧急呼叫联系人
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sos_contact")
public class SosContact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 联系人类型
     */
    private String contactType;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 家庭id
     */
    private Long homeId;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 联系人
     */
    private String username;

    private Long hotelId;

    private Integer sosId;
}
