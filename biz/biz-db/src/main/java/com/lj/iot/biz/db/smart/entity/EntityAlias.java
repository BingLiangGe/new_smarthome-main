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
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2022-11-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("entity_alias")
public class EntityAlias implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实体名称
     */
    private String entityKey;
    /**
     * 实体名称
     */
    private String entityName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 子设备类型
     */
    private String subDeviceType;

    /**
     * 属性类型，device，room
     */
    private String attrType;

    /**
     * 在离线语音词
     */
    private Integer offlineWord;
}
