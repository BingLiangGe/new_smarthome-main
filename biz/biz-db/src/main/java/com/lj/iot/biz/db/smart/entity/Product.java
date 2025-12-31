package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领捷产品表
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "product", autoResultMap = true)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @TableId(value = "product_id", type = IdType.ASSIGN_ID)
    private String productId;

    /**
     * 产品类型（可能是子类型的）
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * '信号类型;IR:红外;RF:射频;MESH 蓝牙MESH,MASTER 主控'
     */
    private String signalType;

    /**
     * 关联设备类型:红外射频设备发送具体的信号从这个字段去关联
     */
    private Long relationDeviceTypeId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品秘钥
     */
    private String productCCCFDF;

    /**
     * 控制器产品ID
     */
    private String controlProductId;

    /**
     * 物模型对象
     */
    @TableField(typeHandler = FastjsonTypeHandler.class, javaType = true)
    private ThingModel thingModel;

    /**
     * 产品图片
     */
    private String imagesUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否需要添加按键数据
     */
    private Boolean isAddKey;

    /**
     * 能否被场景绑定
     */
    private Boolean isShowScene;

    /**
     * app端是否展示 0展示，1不展示
     */
    private Integer isAppShow;

    /**
     * 酒店端是否展示 0展示，1不展示
     */
    private Integer isHotelShow;
}
