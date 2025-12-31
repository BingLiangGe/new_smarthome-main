package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2023-10-19
 */
@Builder
@Getter
@Setter
public class Phonebook implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "phonebook_id", type = IdType.AUTO)
    private Integer phonebookId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 名称
     */
    private String nickname;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
