package com.lj.iot.biz.service.enums;

/**
 * @author 推送消息topic
 * @since 1.0.0
 */
public enum PubTopicEnum {

    /**
     * 发送红外或者设备码
     * {
     * "signalType":"IR",
     * "signal": [89,23,23,...],
     * }
     * sys/${productId}/${deviceId}/thing/service/signal/set
     */
    PUB_IR_OR_RF_CODE("sys/%s/%s/thing/service/signal/set", "发送红外码"),

    /**
     * 登录TOKEN
     */
    PUB_LOGIN_TOKEN("sys/%s/%s/thing/service/login/token", "登录TOKEN"),


    /**
     * mesh设备控制下发
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": { //属性对象
     * "powerstate1": "on", //属性键值对
     * "powerstate2": "off",
     * ...
     * }
     * }
     * sys/${productId}/${deviceId}/thing/service/property/set
     */
    PUB_MESH_PROPERTIES("sys/%s/%s/thing/service/property/set", "设置设备属性"),

    /**
     * mesh设备删除
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": {
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * }
     * }
     * <p>
     * sys/${productId}/${deviceId}/thing/service/topology/delete
     */
    PUB_TOPOLOGY_DELETE("sys/%s/%s/thing/service/topology/delete", "云端删除设备的拓扑关系"),
    /**
     * 通知网关topology关系
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params":[{
     * "productId": "56789", //产品ID
     * "deviceId": "123456" //设备ID
     * }, {
     * "productId": "567891", //产品ID
     * "deviceId": "1234561" //设备ID
     * },
     * ...
     * ]
     * }
     */
    PUB_TOPOLOGY_NOTIFY("sys/%s/%s/thing/service/topology/notify", "通知网关topology关系"),

    /**
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": {
     * "productId": "56789"//产品ID
     * }
     * }
     */
    PUB_TOPOLOGY_ADD("sys/%s/%s/thing/service/topology/add", "云端设备的拓扑关系"),

    /**
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": null
     * }
     */
    PUB_TRIGGER_CLOCK("sys/%s/%s/thing/service/trigger/clock", "触发闹钟"),


    /**
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": null
     * }
     */
    PUB_DEVICE_SCAN("sys/%s/%s/thing/service/device/scan", "请求主动扫描"),
    /**
     * {
     * "id": "123",
     * "code": 0,
     * "data": {
     * "deviceId": "1207d08e7280",//设备名称ID
     * "productId":"",//领捷产品Id
     * "realProductType":"",//产品类型
     * "topProductType":"",//顶级产品类型
     * "productType":"",//产品类型
     * "customName":"",//用户自定义辅助名称
     * "deviceName":"",//用户自定义名称
     * "userId":"",//用户Id
     * "homeId":"",//家Id
     * "roomId":"",//家房间Id
     * "brandId":"",//设备品牌id
     * "brandName":"",//设备品牌名称
     * "masterDeviceId":"",//主控设备Id
     * "masterProductId":"",//主控设备产品Id
     * "modelId":"",//设备型号id
     * "modelName":"",//设备型号名称
     * "signalType":"",//信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     * "status":"",//设备状态在线true,离线false
     * "groupId":"",//组ID
     * "parentId":"",//父ID
     * "physicalDeviceId":"",//物理设备Id
     * "isShow":"",//前端列表是否显示
     * "isShowScene":"",//是否在场景中显示
     * "imagesUrl":"",//图片
     * "controlDeviceId":"",//控制器设备Id
     * "controlProductId":"",//控制器产品ID
     * "isDel":""//是否删除
     * }
     * ,
     * "msg": "success"
     * }
     */
    PUB_DEVICE_UPDATE("sys/%s/%s/thing/service/device/update", "设备消息推送"),

    /**
     * 设备不存在
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": null
     * }
     */
    PUB_TOPOLOGY_NO_EXIST_DEVICE("sys/%s/%s/thing/service/device/no_exist", "设备不存在"),
    /**
     * 设备重启
     */
    PUB_RESTART("sys/%s/%s/thing/service/device/restart", "云端控制设备重启"),

    TRIGGER_REPLY("sys/%s/%s/thing/service/trigger_reply", "下发场景数据"),


    TRIGGER("sys/%s/%s/thing/event/topology/trigger", "场景数据请求"),

    /**
     * mesh 重置
     */
    PUB_RESET("sys/%s/%s/thing/service/device/reset", "云端控制设备mesh重置"),

    /**
     * 设备互控绑定
     */
    PUB_BIND_DEVICE("sys/%s/%s/thing/service/device/bind", "云端控制设备互控绑定"),

    /**
     * 音乐切换
     */
    PUB_MUSIC_CHANGE("sys/%s/%s/thing/service/music/change", "音乐切换"),

    /**
     * 未绑定子设备列表[绑定设备前，请求可绑定的设备]
     */
    TOPOLOGY_MESH_UNBIND("sys/%s/%s/thing/service/topology/mesh/unbind", "未绑定子设备列表[绑定设备前，请求可绑定的设备]"),
    /**
     * 推送房间修改消息到主控
     */
    PUB_HOMEROOM_CHANGE("sys/%s/%s/thing/service/homeroom/change", "房间修改"),

    /**
     * 推送按键数据修改消息到主控
     */
    PUB_RF_KEY_CHANGE("sys/%s/%s/thing/service/rfKey/change", "射频码修改"),

    /**
     * 推送场景修改消息到主控
     */
    PUB_SCENE_CHANGE("sys/%s/%s/thing/service/scene/change", "场景修改"),

    /**
     * 推送场景触发消息到主控
     */
    PUB_SCENE_TRIGGER("sys/%s/%s/thing/service/scene/change", "场景触发"),

    /**
     * 推送设备添加消息到主控
     */
    PUB_DEVICE_ADD("sys/%s/%s/thing/service/device/add", "设备添加"),

    /**
     * 设备变动
     */
    PUB_DEVICE_CHANGE("sys/%s/%s/thing/service/device/change", "设备变动"),

    /**
     * 推送家修改消息到主控
     */
    PUB_HOME_CHANGE("sys/%s/%s/thing/service/home/change", "家修改"),


    /**
     * OTA升级
     */
    PUB_DEVICE_OTA("sys/%s/%s/thing/service/topology/ota", "OTA升级"),


    /**
     * 绑定设备
     */
    ADD_DEVICE_BIND("sys/%s/%s/thing/service/device/bind_3362", "绑定子设备"),


    /**
     * 编辑device
     */
    ADD_DEVICE_EDIT("sys/%s/%s/thing/service/device/edit", "编辑设备"),

    /**
     * 3326推送最新安装包
     */
    PUB_DEVICE_APP("sys/%s/%s/thing/service/topology/app", "3326推送最新安装包"),


    /**
     * 编辑房间
     */
    ADD_ROOM_EDIT("sys/%s/%s/thing/service/room/edit", "编辑房间"),


    /**
     * 射频学码
     */
    ADD_DEVICE_RFDATA("sys/%s/%s/thing/service/device/rfData", "编辑房间"),


    /**
     * 学习射频码
     * {
     * "time": 1524448722000,  //时间
     * "params":{
     * "signalType":"RF", //信号类型  IR\|RF
     * "keyId": 12,  //按键ID
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * }     * "id": "123",//消息ID
     * <p>
     * }
     * sys/${productId}/${deviceId}/thing/service/signal/study
     */
    PUB_RF_CODE("sys/%s/%s/thing/service/signal/study", "学习射频码"),

    /**
     * 设备配网成功上报网络信息
     */
    PUB_SUB_DEVICE_NETWORK("sys/%s/%s/thing/event/topology/network_reply", "设备配网成功上报网络信息"),

    /**
     * 设备绑定成功上报版本
     */
    PUB_SUB_BIND_SUCCESS("sys/%s/%s/thing/event/topology/bind_success_reply", "设备绑定成功上报版本"),

    /**
     * 查询子设备数据
     */
    PUB_SUB_SUB_DEVICE_REPLY("sys/%s/%s/thing/service/topology/subdevice_reply", "查询子设备数据");

    /**
     * TOPIC
     */
    private String topic;

    /**
     * 描述
     */
    private String desc;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    PubTopicEnum(String topic, String desc) {
        this.topic = topic;
        this.desc = desc;
    }

    public static String handlerTopic(PubTopicEnum topicEnum, String productId, String deviceId) {
        return String.format(topicEnum.topic, productId, deviceId);
    }
}
