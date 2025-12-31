package com.lj.iot.biz.db.smart.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2023-08-19
 */
@Data
@Builder
@Getter
@Setter
@TableName("face_user")
public class FaceUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelIgnore
    @TableId(value = "face_id", type = IdType.AUTO)
    private Integer faceId;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", order =1)
    private String faceMobile;

    /**
     * 虚拟手机号
     */
    @ExcelProperty(value = "验证码", order =2)
    private String codes;

    /**
     * 创建时间
     */
    @ExcelIgnore
    private LocalDateTime createTime;
}
