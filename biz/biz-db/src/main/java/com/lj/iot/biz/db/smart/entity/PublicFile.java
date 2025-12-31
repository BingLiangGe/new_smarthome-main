package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * 
 * 产品介绍视频链接表
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("public_file")
public class PublicFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名称
     */
    private String fileName;
}
