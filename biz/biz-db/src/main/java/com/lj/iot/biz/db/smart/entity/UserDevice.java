package com.lj.iot.biz.db.smart.entity;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户设备表
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_device", autoResultMap = true)
public class UserDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称
     */
    @TableId(value = "device_id")
    private String deviceId;

    /**
     * 领捷产品Id
     */
    private String productId;

    /**
     * 产品类型
     */
    private String realProductType;

    /**
     * 顶级产品类型
     */
    private String topProductType;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 用户自定义辅助名称
     */
    private String customName;

    /**
     * 用户自定义名称
     */
    private String deviceName;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 家房间Id
     */
    private Long roomId;

    /**
     * 设备品牌id
     */
    private Long brandId;

    /**
     * 设备品牌名称
     */
    private String brandName;

    /**
     * 主控设备Id
     */
    private String masterDeviceId;

    /**
     * 主控设备产品Id
     */
    private String masterProductId;

    /**
     * 设备型号id
     */
    private Long modelId;

    /**
     * 设备型号名称
     */
    private String modelName;

    /**
     * 信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     */
    private String signalType;

    /**
     * 设备物模型对应的值存储
     */
    @TableField(typeHandler = FastjsonTypeHandler.class, javaType = true)
    private ThingModel thingModel;

    /**
     * mqq属性修改时间
     */
    private LocalDateTime handlerTime;

    /**
     * 设备状态在线true,离线false
     */
    private Boolean status;


    /**
     * 组ID
     */
    private String groupId;

    /**
     * 设备状态变化时间
     */
    private LocalDateTime statusTime;

    /**
     * 父ID
     */
    private String parentId;


    /**
     * 物理设备Id
     */
    private String physicalDeviceId;

    /**
     * 前端列表是否显示（有虚设备的地方主设备不显示）
     */
    private Boolean isShow;

    /**
     * 是否在场景中显示
     */
    private Boolean isShowScene;


    /**
     * 图片
     */
    private String imagesUrl;

    /**
     * 控制器设备Id
     */
    private String controlDeviceId;

    /**
     * 控制器产品ID
     */
    private String controlProductId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 下线时间
     */
    private LocalDateTime downTime;


    /**
     * 是否删除
     */
    private Boolean isDel;

    /**
     * 门锁密码
     */
    private String doorPwd;

    /**
     * 房间名称
     */
    @TableField(exist = false)
    private String roomName;


    /**
     * 关联设备类型:红外射频设备发送具体的信号从这个字段去关联
     */
    @TableField(exist = false)
    private Long relationDeviceTypeId;

    /**
     * 虚设备
     */
    @TableField(exist = false)
    private List<UserDevice> subUserDeviceList;


    /**
     * 子设备长度
     */
    @TableField(exist = false)
    private int UserDeviceSize;

    /**
     * 自定义模式
     */
    @TableField(exist = false)
    List<UserDeviceMode> userDeviceModeList;

    private String hardWareVersion;
    private String softWareVersion;
    private String bluetoothVersion;

    /**
     * 设备延时
     */
    @TableField(exist = false)
    private Integer delayedTime = 0;

    /**
     * 彩灯-亮度
     */
    private Integer saturation;

    /**
     * 彩灯-颜色
     */
    private Integer hue;

    /**
     * 回复消息 时间
     */
    private LocalDateTime messageTime;

    /**
     * wifi名称
     */
    private String wifiName;

    /**
     * wifi密码
     */
    private String wifiPwd;

    /**
     * wifi信号值
     */
    private Integer wifiSs;

    /**
     * wifi信号登记
     */
    private Integer wifiLevel;

    /**
     * 设备appkey
     */
    private String appKey;


    /**
     * 设备networkkey
     */
    private String netWorkKey;


    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * 音量
     */
    private Integer volume;

    private String latitude;

    private String longitude;

    private Integer heartRate;

    private BigDecimal temperature;

    private String bloodPressure;

    private Integer bloodOxygen;

    private String address;

    private String lockCCCFDF;

    private String lockAuthCode;

    private Integer radius;

    private String settingLat;

    private String settingLng;

    private Integer watchStatus;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private Integer isTrigger;

    @TableField(exist = false)
    private String mobile;
}
