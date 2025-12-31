package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-02-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_messages")
public class SystemMessagesVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fomTime;

    private List<SystemMessages> list;


}
