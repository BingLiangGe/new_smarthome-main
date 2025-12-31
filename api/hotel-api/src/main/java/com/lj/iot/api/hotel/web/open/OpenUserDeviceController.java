package com.lj.iot.api.hotel.web.open;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户设备相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/open/user_device")
public class OpenUserDeviceController {

    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Resource
    IUserDeviceService userDeviceService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private ICacheService cacheService;


    @Autowired
    private BizWsPublishService bizWsPublishService;


    /**
     * 绑定主控上报
     *
     * @return
     */
    @RequestMapping("/bindMasterDevice")
    public CommonResultVo<String> bindMasterDevice(String masterDeviceId) {
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");

        String key = "bind_master_device_" + masterDeviceId;
        String datas = cacheService.get(key);

        ValidUtils.isNullThrow(datas, "datas为空");

        log.info("872进入绑定主控上报,deviceId={},userId={}", masterDeviceId, datas);

        String userId=datas.split(",")[0];
        String homeId=datas.split(",")[1];

        bizWsPublishService.publish(WsResultVo.SUCCESS(userId,
                Long.valueOf(homeId),
                RedisTopicConstant.TOPIC_CHANNEL_MASTER_DEVICE_BIND,
                masterDeviceId
        ));

        return CommonResultVo.SUCCESS();
    }

    /**
     * @param deviceId
     * @return
     */
    @ResponseBody
    @RequestMapping("/unBindDevice")
    public CommonResultVo<String> unBindDevice(String deviceId) {
        UserDevice selectUserDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(deviceId)
                .build()));

        ValidUtils.isNullThrow(selectUserDevice, "设备数据不存在");

        // 主控
        UserDevice masterDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(selectUserDevice.getMasterDeviceId())
                .build()));

        ValidUtils.isNullThrow(masterDevice, "主控不存在操作!");

        if (selectUserDevice.getDeviceId().equals(masterDevice.getDeviceId())) {
            ValidUtils.isFalseThrow(false, "主控设备不允许删除!");
        }

        // 无权操作其他酒店设备
        if (!"1.3".equals(masterDevice.getHardWareVersion()) || masterDevice.getHardWareVersion() == null) {
            ValidUtils.isFalseThrow(false, "越权操作!");
        }

        DeviceIdDto dto = DeviceIdDto.builder()
                .deviceId(deviceId).build();

        UserDevice userDevice = bizUserDeviceService.delete(dto, selectUserDevice.getUserId());

        ValidUtils.isNullThrow(userDevice, "设备不存在");

        // 主控删除升级队列
        if ("MASTER".equals(userDevice.getSignalType())) {
            upgradeRecordService.remove(new QueryWrapper<>(UpgradeRecord.builder().deviceId(userDevice.getDeviceId()).build()));
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 发送数据
     *
     * @param dto
     * @return
     */
    @PostMapping("send_data")
    public CommonResultVo<String> sendData(@RequestBody @Valid SendDataDto dto) {
        bizUserDeviceService.sendData(dto, OperationEnum.APP_C);
        return CommonResultVo.SUCCESS();
    }
}
