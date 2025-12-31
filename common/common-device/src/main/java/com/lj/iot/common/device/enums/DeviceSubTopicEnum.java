package com.lj.iot.common.device.enums;

/**
 * 订阅topic的枚举类
 */
public enum DeviceSubTopicEnum {
    /**
     * 救援
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": null
     * }
     */
    EVENT_SOS("thing/event/sos/post", "sys/%s/%s/thing/event/sos/post_reply", "救援"),


    /**
     * 救援
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": null
     * }
     */
    LOCK("thing/service/device/lock_reply", "", "设备锁定状态上报"),


    /**
     * 学码响应
     */
    SERVICE_SIGNAL_STUDY_REPAY("thing/service/signal/study_reply", "", "学码响应"),

    /**
     * 子设备在线状态
     * {
     * "time":1659418371418,
     * "status":true
     * }
     */
    EVENT_SUB_DEVICE_LINE("thing/event/topology/line", "sys/%s/%s/thing/event/topology/line_reply", "子设备在线状态"),

    /**
     * 离线红外码
     */
    EVENT_SUB_DEVICE_OFFLINE("thing/event/offline/code", "sys/%s/%s/thing/event/offline/code_reply", "设备红外离线码"),

    /**
     * 播报上报
     */
    EVENT_SUB_PLAYER_REPLY("thing/service/device/player_reply", "", "播报上报"),

    /**
     * 设备配网成功上报网络信息
     */
    EVENT_SUB_DEVICE_NETWORK("thing/event/topology/network", "sys/%s/%s/thing/event/offline/network_reply", "设备配网成功上报网络信息"),

    /**
     * 设备绑定成功上报版本
     */
    EVENT_SUB_DEVICE_BIND_SUCCESS("thing/event/topology/bind_success", "sys/%s/%s/thing/event/offline/bind_success_reply", "设备绑定成功上报版本"),


    /**
     * 查询子设备数据
     */
    PUB_SUB_SUB_DEVICE("thing/service/topology/subdevice", "sys/%s/%s/thing/service/topology/subdevicet_reply", "查询子设备数据"),


    /**
     * 获取设备三元组hash
     */
    EVENT_DEVICE_HASH("thing/event/device/hash", "sys/%s/%s/thing/event/device/hash_reply", "子设备三元组hash"),

    /**
     * 网关获取topology关系
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params":null
     * }
     */
    EVENT_TOPOLOGY_LIST("thing/event/topology/list", "sys/%s/%s/thing/event/topology/list_reply", "网关获取topology关系"),

    /**
     * 子设备的MQTT动态注册
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": [{
     * "productId": "56789", //产品ID
     * "macAddress": "dsfasdfasdf", //mac 地址
     * }
     * ]
     * }
     */
    EVENT_SUB_DEVICE_REGISTER("thing/event/register", "sys/%s/%s/thing/event/register_reply", "子设备的MQTT动态注册"),


    /**
     * mesh设备添加响应
     * {
     * "id": "123", //消息ID
     * "code": 0, //0:成功  -1:失败
     * "data": {
     * "productId": "56789", //产品ID
     * "deviceId": "123456" //设备ID
     * },
     * "msg": "success", //消息描述
     * }
     */
    SERVICE_TOPOLOGY_ADD_REPLY("thing/service/topology/add_reply", "mesh设备添加响应"),


    SERVICE_TOPOLOGY_OTA_REPLY("thing/service/topology/ota_reply", "OTA升级"),

    SERVICE_LOGIN_TOKEN_REPLY("thing/service/login/token_reply", "获取第一次信息"),

    /**
     * 获取音乐歌单
     */
    SERVICE_MUSIC_CHANGE_REPLY("thing/service/music/change_reply", "音乐返回"),


    /**
     * 获取扫码设备三元组hash
     */
    PUB_DEVICE_HASH("thing/service/device/hash", "获取扫码设备三元组hash"),

    /**
     * mesh设备添加删除
     * {
     * "id": "123", //消息ID
     * "code": 0, //0:成功  -1:失败
     * "msg": "success", //消息描述
     * "data": {
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * }
     * }
     * sys/${productId}/${deviceId}/thing/service/topology/delete_reply
     */
    SERVICE_TOPOLOGY_DELETE_REPLY("thing/service/topology/delete_reply", "mesh设备删除响应"),

    /**
     * 设备上报属性
     * {
     * "id": "123", //消息ID
     * "code": 0, //0:成功  -1:失败
     * "data":  { //属性对象
     * "productId":"56789",
     * "deviceId":"123456",
     * "properties":[{
     * "identifier":"powerstate_1",
     * "value":"0"
     * }],//属性键
     * ...
     * },
     * "msg": "success" //消息
     * }
     */
    EVENT_PROPERTIES_POST("thing/event/property/post", "sys/%s/%s/thing/event/property/post_reply", "设备上报属性"),


    /**
     * 设置设备属性响应
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": { //属性对象
     * "powerstate1": "on", //属性键值对
     * "powerstate2": "off",
     * ...
     * }
     * }
     */
    SERVICE_PROPERTIES_SET_REPLY("thing/service/property/set_reply", "设置设备属性响应"),

    /**
     * 互联设备绑定响应
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "code": 0, //0:成功  -1:失败
     * "data": { //属性对象
     * "groupId": "232435325", //属性键值对
     * "deviceList": [{"deviceId":"eewq"
     * "properties":"powerstate"
     * }],
     * ...
     * }
     * }
     */
    SERVICE_DEVICE_BIND_REPLY("thing/service/device/bind_reply", "互联设备绑定响应"),


    /**
     * 闹钟返回reply
     */
    SERVICE_CLOCK_REPLY("thing/service/trigger/clock_reply", "闹钟返回监听"),

    /**
     * 未绑定子设备列表
     * {
     * "id": "123", //消息ID
     * "code": 0, //0:成功  -1:失败
     * "data": [{
     * "productId": "56789", //产品ID
     * "deviceId": "123456" //设备ID
     * }, {
     * "productId": "567891", //产品ID
     * "deviceId": "1234561" //设备ID
     * },
     * ...
     * ],
     * "msg": "success", //消息描述
     * }
     */
    SERVICE_MESH_UNBIND_REPLY("thing/service/topology/mesh/unbind_reply", "未绑定子设备列表"),

    /**
     * 网关删除设备的拓扑关系
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": {
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * }
     * }
     */
    EVENT_TOPOLOGY_DELETE("thing/event/topology/delete", "sys/%s/%s/thing/event/topology/delete_reply", "主控设备topology删除上报"),

    /**
     * 设备上线
     */
    EVENT_SYS_CONNECTED("$SYS/brokers/connected", "emqx系统设备上线"),

    /**
     * 设备离线
     */
    EVENT_SYS_DISCONNECTED("$SYS/brokers/disconnected", "emqx系统设备离线"),


    /**
     * 主控设备topology维护结果上报
     * {
     * "time":1659418371418,
     * "msgId":"uuid4",
     * "status":true
     * }
     */
    TOPOLOGY_ACK("device/event/topologyAck", "主控设备topology维护结果上报"),

    /**
     * 设备属性[按键]绑定结果上报
     * {
     * "time":1659418371418,
     * "msgId": "uuid3",
     * "status": true
     * }
     */
    BIND_PROPERTY_ACK("device/event/bindPropertyAck", "设备属性[按键]绑定结果上报"),


    /**
     * 默认
     */
    DEFAULT("topic/default", "默认");




    /**
     * TOPIC
     */
    private String topic;

    /**
     * TOPIC_REPLAY
     */
    private String topicReply;
    /**
     * 描述
     */
    private String desc;

    public String getTopicReply() {
        return topicReply;
    }

    public void setTopicReply(String topicReply) {
        this.topicReply = topicReply;
    }

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

    DeviceSubTopicEnum(String topic, String desc) {
        this.topic = topic;
        this.desc = desc;
    }

    DeviceSubTopicEnum(String topic, String topicReplay, String desc) {
        this.topic = topic;
        this.topicReply = topicReplay;
        this.desc = desc;
    }

    public static DeviceSubTopicEnum parse(String topic) {
        for (DeviceSubTopicEnum item : DeviceSubTopicEnum.values()) {
            if (item.topic.equals(topic)) {
                return item;
            }
        }
        return DEFAULT;
    }
}