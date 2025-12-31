package com.lj.iot.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.MasterDeviceDto;
import com.lj.iot.biz.base.vo.SceneAddDeviceListVo;
import com.lj.iot.biz.base.vo.UserDeviceBindVo;
import com.lj.iot.biz.base.vo.UserDeviceStatisticsVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.dto.LockMasterDeviceDto;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.SendVoiceDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.mqtt.client.core.HandleMessage;

import java.util.List;
import java.util.Map;

/**
 * 用户设备表
 */
public interface BizUserDeviceService {

    /**
     * 添加设备
     *
     * @return
     */
    UserDevice add(UserDeviceAddDto dto, String userId);


    UserDevice addDeviceTest(UserDeviceAddDto dto, String userId);

    UserDevice hoteladd(UserDeviceAddDto dto, String userId);

    void searchNewMesh(SearchMeshDeviceDto dto, String userId);

    void edit(List<UserDeviceEditDto> dto, String userId);


    void editFourFriends(List<UserDeviceEditDto> dto, String userId);
    void hotelEdit(List<UserDeviceEditDto> dto, String userId);


    UserDevice deleteFourFriend(DeviceIdDto dto, String userId);

    UserDevice delete(DeviceIdDto dto, String userId);

    UserDevice hotleDelete(DeviceIdDto dto, String userId);

    void delete(UserDevice userDevice);

    /**
     * @param pageDto
     * @return
     */
    IPage<UserDevice> customPage(UserDevicePageDto pageDto);

    UserDeviceStatisticsVo statistics(UserDevicePageDto pageDto);

    /**
     * 主控设备添加
     *
     * @param masterDeviceDto
     */
    UserDevice addMasterDevice(MasterDeviceDto masterDeviceDto, String userId);


    /**
     * 主控设备添加
     *
     * @param masterDeviceDto
     */
    UserDevice hotelAddMasterDevice(MasterDeviceDto masterDeviceDto, String userId);

    /**
     * 主控重启
     *
     * @param deviceIdDto
     */
    void restartMasterDevice(DeviceIdDto deviceIdDto, String userId);

    /**
     * 重置mesh[删除所有mesh设备]
     *
     * @param deviceIdDto
     */
    void resetMesh(DeviceIdDto deviceIdDto, String userId);

    /**
     * 主控设备TOPO添加
     *
     * @param message
     */
    void topologyAddDevice(HandleMessage message);

    /**
     * 发送数据
     *
     * @param dto
     */
    void sendData(SendDataDto dto, OperationEnum operationEnum);

    void handleList(List<HandleUserDeviceDto<UserDevice>> handleList, OperationEnum operationEnum);


    void handle(UserDevice userDevice, ThingModel thingModel, String keyCode, OperationEnum operationEnum);

    /**
     * 保存属性，并返回改变的属性键值对
     *
     * @param userDevice
     * @param map
     * @return
     */
    void saveTogetherProperties(UserDevice userDevice, Map<String, Object> map);

    void saveTogetherPropertiesWithJson(UserDevice userDevice, JSONObject paramJson);

    /**
     * 获取绑定设备列表
     *
     * @param dto
     * @return
     */
    List<UserDeviceBindVo> showBindList(UserDeviceBindDto dto, String userId);

    /**
     * 安卓获取绑定设备列表
     *
     * @param dto
     * @return
     */
    List<UserDeviceBindVo> androidshowBindList(UserDeviceBindDto dto);

    /**
     * 切换组
     * @param dto
     * @param uId
     */
    void switchGroup(SwitchGroupsDto dto, String uId);

    /**
     * 设备绑定
     *
     * @param dto
     * @param uId
     */
    List<String> bindDevice(DeviceBindDto dto, String uId);

    /**
     * 互联设备数据绑定更新
     *
     * @param userDevice
     * @param data
     */
    void updateBindDevice(UserDevice userDevice, Object data);


    List<SceneAddDeviceListVo> sceneAddDeviceList(SceneAddDeviceDto dto, String userId);

    /**
     * 一键删除多联多控
     * @param dto
     * @param uId
     */
    void delBindDevice(DelDeviceBindDto dto, String uId);

    /**
     * 一键删除多联多控
     * @param dto
     * @param uId
     */
    void sysDelBindDevice(DelDeviceBindDto dto, String uId);

    /**
     * 音乐切换
     * @param type
     * @param deviceId
     */
    void change(String type, String deviceId,String musicId,String volume);

    /**
     * 根据家ID获取主控设备数据集合
     * @param homeId
     * @return
     */
    List<UserDevice> getMasterUserDeviceByHomeId(Long homeId,String userId);

    /**
     * 获取设备数据
     * @param masterDeviceId
     * @param deviceId
     * @return
     */
    List<UserDevice> OfflineList(String masterDeviceId, String deviceId);

    /**
     * 播放音频
     * 1-十五分钟结束-岭捷
     * 2-五分钟结束-岭捷
     * 3-欢迎光临-岭捷
     * 4-结束-岭捷
     * 11-十五分钟结束-通用
     * 12-五分钟结束-通用
     * 13-欢迎光临-通用
     * 14-结束-通用
     *
     * @param dto
     * @return
     */
    void sendVoice(SendVoiceDto dto);

    /**
     *
     * @param dto
     */
    void lockMasterDevice(LockMasterDeviceDto dto);
}
