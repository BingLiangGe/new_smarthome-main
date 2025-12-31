package com.lj.iot.biz.service.mqtt.push.service;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.SendUserDeviceBindVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.ThingModel;

import java.util.List;

public interface MqttPushService {


     void pushIRCode(UserDevice masterDevice, SignalEnum ir, String signal, Object extendData);

    /**
     * 测试红外码
     *
     * @param masterDevice 主控设备
     * @param ir           信号类型
     * @param signal       红外码
     */
    void pushFROrIRCode(UserDevice masterDevice, SignalEnum ir, String[] signal, Object extendData);


    void pushFROrIRCodeTrigger(UserDevice masterDevice, SignalEnum ir, String[] signal, Object extendData, Integer isTrigger);

    /**
     * 扫描登录TOKEN
     *
     * @param masterDevice
     * @param extendData
     */
    void pushLoginToken(UserDevice masterDevice, Object extendData);

    /**
     * mesh设备属性下发
     *
     * @param
     */
    void pushMeshProperties(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum);

    void pushMethMajongMachine(UserDevice masterUserDevice, UserDevice userDevice, String[] data, String identifier, ThingModel properties, OperationEnum operationEnum);


    void pushMeshHeatingTable(UserDevice masterUserDevice, UserDevice userDevice, String commend, String identifier, ThingModel properties, OperationEnum operationEnum);

    /**
     * mesh设备属性下发-转义
     *
     * @param
     */
    void pushMeshPropertiesCase(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum, JSONObject valueJson);

    /**
     * 单火二路和单火三路mesh设备属性下发
     *
     * @param
     */
    void pushMeshSwitchProperties(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum);

    /**
     * 射频学码
     *
     * @param
     * @return
     */
    void signalStudy(UserDevice masterUserDevice, Object params);

    /**
     * 通知网关topology关系
     *
     * @param
     * @return
     */
    void notifyTopology(UserDevice masterUserDevice);

    /**
     * 重置mesh
     *
     * @param masterUserDevice
     */
    void reset(UserDevice masterUserDevice);

    /**
     * 删除设备
     *
     * @param masterUserDevice
     * @param userDevice
     */
    void delete(UserDevice masterUserDevice, UserDevice userDevice);

    /**
     * 添加设备
     *
     * @param masterUserDevice
     * @param params
     */
    void addTopology(UserDevice masterUserDevice, Object params);

    /**
     * 不存在设备
     *
     * @param productId
     * @param deviceId
     */
    void noExistDevice(String productId, String deviceId);

    /**
     * @param productId
     * @param deviceId
     */
    void noExistDevice(String productId, String deviceId, Object data);

    /**
     * 重启主控
     */
    void restartMasterDevice(UserDevice masterUserDevice);

    /**
     * 发送绑定的设备信息数据
     *
     * @param userDevice
     * @param sendUserDeviceBindVos
     */
    void sendBindDevice(UserDevice userDevice, List<SendUserDeviceBindVo> sendUserDeviceBindVos, String groupId);

    void searchNewMesh(UserDevice masterUserDevice, String productId);

    void push(UserDevice userDevice, PubTopicEnum topicEnum, Object params);

    /**
     * 音乐切换
     *
     * @param userDevice
     * @param type
     */
    void musicChange(UserDevice userDevice, String type, String musicId, String volume);

    /**
     * 下发音乐列表
     *
     * @param userDevice
     * @param list
     */
    void musicMenu(UserDevice userDevice, List<MusicMenuTop> list);

    /**
     * 推送家数据
     *
     * @param home
     * @param type
     * @param userDevice
     */
    void pushOfficeHomeData(Home home, String type, UserDevice userDevice);

    /**
     * 推送房间编辑功能
     *
     * @param homeRoom
     * @param s
     * @param it
     */
    void pushOfficeHomeRoomData(HomeRoom homeRoom, String s, UserDevice it);

    /**
     * 新增场景触发
     *
     * @param dto
     * @param code
     * @param it
     */
    void pushOfficeSceneData(Object dto, String code, UserDevice it);

    /**
     * 离线数据推送
     *
     * @param masterUserDevice
     * @param offlineTypeEnum
     * @param pubTopicEnum
     * @param data
     */
    void pushOfficeData(UserDevice masterUserDevice, OfflineTypeEnum offlineTypeEnum, PubTopicEnum pubTopicEnum, Object data);

    /**
     * 离线数据推送【家庭下的所有主控】
     *
     * @param homeId
     * @param offlineTypeEnum
     * @param pubTopicEnum
     * @param data
     */
    void pushOfficeData(Long homeId, OfflineTypeEnum offlineTypeEnum, PubTopicEnum pubTopicEnum, Object data);

    /**
     * 触发场景推送消息给主控
     *
     * @param sceneId
     * @param code
     * @param it
     */
    void pushOfficeSceneTriggerData(Object sceneId, String code, UserDevice it);

    /**
     * 添加设备推送消息给主控
     *
     * @param dto
     * @param code
     * @param it
     */
    void pushOfficeAddDeviceData(Object dto, String code, UserDevice it);

    /**
     * 学码成功推送消息至主控
     *
     * @param dto
     * @param code
     * @param it
     */
    void pushOfficeSignalChange(Object dto, String code, UserDevice it);

    void scanDevice(String masterProductId, String productId, String masterDeviceId);

    /**
     * 设备消息推送
     *
     * @param userDevice
     */
    void pushDevice(UserDevice userDevice, String masterProductId, String masterDeviceId);
}
