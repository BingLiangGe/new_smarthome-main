package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 
 * 历史通话记录
 *
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("communicate_log_his")
public class CommunicateLogHis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 家ID
     */
    private Long homeId;

    /**
     * 求救用户名称
     */
    private String homeName;

    /**
     * 求救状态，0成功，1失败
     */
    private String flag;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 联系人姓名
     */
    private String contactsName;

    /**
     * 房间名
     */
    private String roomName;

    /**
     * 调用信息
     */
    private String msg;

    /**
     * 返回码
     */
    private String code;
}
