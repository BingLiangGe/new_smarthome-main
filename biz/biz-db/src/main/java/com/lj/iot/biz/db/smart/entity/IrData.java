package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ir_data")
public class IrData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 文件编号
     */
    private String fileId;

    /**
     * 索引
     */
    private String dataIndex;

    /**
     * 红外码
     */
    private String irData;
}
