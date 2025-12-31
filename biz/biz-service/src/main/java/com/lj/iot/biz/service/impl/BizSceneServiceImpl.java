package com.lj.iot.biz.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.AuthSceneVo;
import com.lj.iot.biz.base.vo.OfflineSceneListVo;
import com.lj.iot.biz.base.vo.SceneDetailVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.mapper.SceneMapper;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.CronExpression;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.fegin.job.JobFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 场景业务处理层
 */
@Slf4j
@Service
public class BizSceneServiceImpl implements BizSceneService {

    @Resource
    ISceneService sceneService;

    @Resource
    MqttPushService mqttPushService;

    @Resource
    IProductTypeService productTypeService;

    @Autowired
    private ISceneDeviceService sceneDeviceService;

    @Autowired
    private ISceneScheduleService sceneScheduleService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IHomeService homeService;

    @Resource
    BizUploadEntityService bizUploadEntityService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Resource
    private JobFeignClient jobFeignClient;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private IProductThingModelKeyService productThingModelKeyService;

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;
    @Resource
    private IDeviceGroupService iDeviceGroupService;

    @Override
    public List<Scene> list(Long homeId) {
        return sceneService.list(new QueryWrapper<>(Scene.builder()
                .homeId(homeId)
                .build()).orderByAsc("create_time"));
    }

    @Override
    public List<Scene> list(Long homeId, String userId) {
        return sceneService.list(new QueryWrapper<>(Scene.builder()
                .homeId(homeId)
                .userId(userId)
                .build()));
    }

    @Override
    public List<Scene> authList(Long homeId, String userId) {
        List<Scene> list = sceneService.authList(homeId, userId);

        list.forEach(scene -> {
            if (StringUtils.isBlank(scene.getSceneIcon())) {
                scene.setSceneIcon("https://img.lj-smarthome.com/scene_img/1.png");
            }
        });

        return list;
    }

    @Override
    public Scene add(SceneAddDto dto, String userId) {

        return add(dto, userId, true);
    }

    @Override
    public Scene add(SceneAddDto dto, String userId, Boolean uploadEntity) {
        if (StringUtils.isNotEmpty(dto.getMasterId())) {
            String[] split = dto.getMasterId().split(",");
            for (String masterId : split) {
                ValidUtils.isNullThrow(userDeviceService.findDeviceByDeviceIdAndUserId(masterId, userId), "主控设备ID有误");
            }
        }

        Home home = homeService.getOne(new QueryWrapper<>(Home.builder()
                .userId(userId)
                .id(dto.getHomeId())
                .build()));
        ValidUtils.isNullThrow(home, "家庭数据不存在");

        //设置默认的场景名做口令
        String command = dto.getCommand();
        if (!command.startsWith(dto.getSceneName())) {
            command = command + "," + dto.getSceneName();
        }

        //保存场景数据
        Scene scene = Scene.builder()
                .userId(userId)
                .masterId(dto.getMasterId())
                .homeId(dto.getHomeId())
                .sceneIcon(dto.getSceneIcon())
                .sceneName(dto.getSceneName())
                .command(command)
                .build();
        sceneService.save(scene);
        //保存场景设备数据
        updateSceneDevice(scene.getId(), dto.getSceneDevices(), userId);

        //保存场景调度数据
        updateSceneSchedule(scene.getId(), dto.getSceneScheduleList());

        //上传动态实体
        if (uploadEntity) {
            bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.SceneCorpus);
        }
        //推送消息到互控设备
        List<UserDevice> maUserDeviceList = bizUserDeviceService.getMasterUserDeviceByHomeId(home.getId(), userId);
        maUserDeviceList.stream().forEach(it -> {
            mqttPushService.pushOfficeSceneData(scene.getId(), OfflineTypeEnum.OFFLINE_ADD.getCode(), it);

            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.TRIGGER, it.getProductId(), it.getMasterDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data("send")
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send_trigger:" + topic + "=" + JSON.toJSONString(paramDto));
        });
        return scene;
    }

    @Override
    public Scene edit(SceneEditDto dto, String userId) {

        return edit(dto, userId, true);
    }

    @Override
    public Scene edit(SceneEditDto dto, String userId, Boolean uploadEntity) {
        if (StringUtils.isNotEmpty(dto.getMasterId())) {
            String[] split = dto.getMasterId().split(",");
            for (String masterId : split) {
                ValidUtils.isNullThrow(userDeviceService.findDeviceByDeviceIdAndUserId(masterId, userId), "主控设备ID有误");
            }
        }

        Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(dto.getSceneId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(scene, "数据不存在");

        //设置默认的场景名做口令
        String command = scene.getCommand();

        if (!scene.getCommand().equals(dto.getCommand())) {
            command = dto.getCommand();
            if (!command.startsWith(dto.getSceneName())) {
                command = command + "," + dto.getSceneName();
            }
        } else {
            if (!scene.getSceneName().equals(dto.getSceneName())) {

                if(!Arrays.asList(command.split(",")).contains(dto.getSceneName())){
                    command = command + "," + dto.getSceneName();
                }
            }
        }


        //保存场景数据
        sceneService.updateById(Scene.builder()
                .sceneName(dto.getSceneName())
                .masterId(dto.getMasterId())
                .id(dto.getSceneId())
                .sceneIcon(dto.getSceneIcon())
                .command(command)
                .build());
        //保存场景设备数据
        updateSceneDevice(scene.getId(), dto.getSceneDevices(), userId);

        //保存场景调度数据
        updateSceneSchedule(scene.getId(), dto.getSceneScheduleList());

        //上传动态实体
        if (uploadEntity) {
            bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.SceneCorpus);
        }
        //推送消息到互控设备
        List<UserDevice> maUserDeviceList = bizUserDeviceService.getMasterUserDeviceByHomeId(scene.getHomeId(), userId);
        maUserDeviceList.stream().forEach(it -> {
            mqttPushService.pushOfficeSceneData(scene.getId(), OfflineTypeEnum.OFFLINE_ADD.getCode(), it);

            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.TRIGGER, it.getProductId(), it.getMasterDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data("send")
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send_trigger:" + topic + "=" + JSON.toJSONString(paramDto));
        });
        return sceneService.getById(scene.getId());
    }

    @Override
    public void deleteScene(Long sceneId, String userId) {
        //判断用户是否为家管理员
        Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(sceneId)
                .userId(userId).build()));
        ValidUtils.isNullThrow(scene, "数据不存在");

        if (scene.getIsDefault() == 1) {
            ValidUtils.isFalseThrow(scene.getIsDefault() != 1, "预设场景不能被删除");
        }

        //删除缓存
        sceneService.deleteCacheById(sceneId);

        sceneService.removeById(sceneId);

        sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder()
                .sceneId(sceneId)
                .build()));

        sceneScheduleService.remove(new QueryWrapper<>(SceneSchedule.builder()
                .sceneId(sceneId)
                .build()));

        //场景的按键数据置为0
        userDeviceMeshKeyService.update(UserDeviceMeshKey.builder()
                        .sceneId(0L)
                        .build(),
                new QueryWrapper<>(UserDeviceMeshKey.builder()
                        .sceneId(sceneId)
                        .build()));

        //删除定时触发器
        jobFeignClient.deleteSceneJob(IdDto.builder().id(sceneId).build());

        //上传动态实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.SceneCorpus);

        //推送消息到互控设备
        List<UserDevice> maUserDeviceList = bizUserDeviceService.getMasterUserDeviceByHomeId(scene.getHomeId(), userId);
        maUserDeviceList.stream().forEach(it -> {
            mqttPushService.pushOfficeSceneData(sceneId, OfflineTypeEnum.OFFLINE_ADD.getCode(), it);

            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.TRIGGER, it.getProductId(), it.getMasterDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data("send")
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send_trigger:" + topic + "=" + JSON.toJSONString(paramDto));
        });
    }


    @Override
    public void deleteHomeScene(Long sceneId, String userId) {
        //判断用户是否为家管理员
        Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(sceneId)
                .userId(userId).build()));
        ValidUtils.isNullThrow(scene, "数据不存在");

//          测试说场景不能删除
//        //删除缓存
//        sceneService.deleteCacheById(sceneId);
//
//        sceneService.removeById(sceneId);
//
//        sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder()
//                .sceneId(sceneId)
//                .build()));
//
//        sceneScheduleService.remove(new QueryWrapper<>(SceneSchedule.builder()
//                .sceneId(sceneId)
//                .build()));

        //场景的按键数据置为0
        userDeviceMeshKeyService.update(UserDeviceMeshKey.builder()
                        .sceneId(0L)
                        .build(),
                new QueryWrapper<>(UserDeviceMeshKey.builder()
                        .sceneId(sceneId)
                        .build()));

        //删除定时触发器
        jobFeignClient.deleteSceneJob(IdDto.builder().id(sceneId).build());

        //上传动态实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.SceneCorpus);

        //推送消息到互控设备
        List<UserDevice> maUserDeviceList = bizUserDeviceService.getMasterUserDeviceByHomeId(scene.getHomeId(), userId);
        maUserDeviceList.stream().forEach(it -> {
            mqttPushService.pushOfficeSceneData(sceneId, OfflineTypeEnum.OFFLINE_ADD.getCode(), it);
        });
    }

    @Override
    public void deleteSceneByHomeId(Long homeId, String userId) {
        //查询家下所有场景数据
        List<Scene> scenes = sceneService.list(new QueryWrapper<>(Scene.builder()
                .homeId(homeId).build()).select("id"));
        Optional.ofNullable(scenes).ifPresent(sceneList -> {
            for (Scene it : sceneList) {
                //deleteScene(it.getId(), userId);
                deleteHomeScene(it.getId(), userId);
            }
        });
    }

    private void updateSceneDevice(Long sceneId, List<SceneDeviceDto> sceneDeviceDtoList, String userId) {

        sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder()
                .sceneId(sceneId)
                .build()));


        List<SceneDevice> sceneDeviceList = new ArrayList<>();
        //设备列表按照延时时间长短进行排序
        //sceneDeviceDtoList.sort(Comparator.comparing(SceneDeviceDto::getDelayedTime));

        for (SceneDeviceDto sceneDeviceDto : sceneDeviceDtoList) {
            UserDevice userDevice = userDeviceService.findDeviceByDeviceIdAndUserId(sceneDeviceDto.getDeviceId(), userId);
            ValidUtils.isNullThrow(userDevice, "设备数据错误");

            //排除不能场绑定场景的设备
            ValidUtils.isFalseThrow(userDevice.getIsShowScene(), userDevice.getCustomName() + "不能绑定场景");

            //匹配thingModelKey
            ThingModelProperty thingModelProperty = sceneDeviceDto.getThingModel().getProperties().get(0);

            //补充扩展 dataType 和name
            sceneDeviceDto.getThingModel().thingModelPropertyExtend(userDevice.getThingModel());

            // 窗帘
            if ("curtain".equals(userDevice.getProductType())) {
                ThingModel newModel = new ThingModel();

                List<ThingModelProperty> properties = new ArrayList<>();

                properties.add(sceneDeviceDto.getThingModel().getProperties().get(0));

                newModel.setProperties(properties);

                sceneDeviceDto.setThingModel(newModel);
            }

            SceneDevice sceneDevice = SceneDevice.builder()
                    .sceneId(sceneId)
                    .productId(userDevice.getProductId())
                    .productType(userDevice.getProductType())
                    .deviceId(sceneDeviceDto.getDeviceId())
                    .thingModel(sceneDeviceDto.getThingModel())
                    .delayedTime(sceneDeviceDto.getDelayedTime())
                    .build();

            //射频红外需要获取keyCode
            if (SignalEnum.IR.getCode().equals(userDevice.getSignalType())
                    || SignalEnum.RF.getCode().equals(userDevice.getSignalType())) {

                Object value = thingModelProperty.getValue();

                ProductThingModelKey productThingModelKey = productThingModelKeyService.getProductThingModelKey(userDevice.getProductId(),
                        userDevice.getModelId(), thingModelProperty.getIdentifier(), Double.valueOf(value + "").intValue());
                ValidUtils.isNullThrow(productThingModelKey, "参数错误");
                sceneDevice.setKeyCode(productThingModelKey.getKeyCode());
            }

            sceneDeviceList.add(sceneDevice);
        }

        if (sceneDeviceList.size() != 0) {
            sceneDeviceService.saveBatch(sceneDeviceList);
        }
    }

    private void updateSceneSchedule(Long sceneId, List<SceneScheduleDto> sceneScheduleDtoList) {

        //删除定时触发器
        jobFeignClient.deleteSceneJob(IdDto.builder().id(sceneId).build());

        sceneScheduleService.remove(new QueryWrapper<>(SceneSchedule.builder()
                .sceneId(sceneId)
                .build()));

        for (SceneScheduleDto sceneScheduleDto : sceneScheduleDtoList) {
            ValidUtils.isFalseThrow(CronExpression.isValidExpression(sceneScheduleDto.getCron())
                    , "cron表达式错误");
            SceneSchedule sceneSchedule = SceneSchedule.builder()
                    .sceneId(sceneId)
                    .enable(sceneScheduleDto.getEnable())
                    .cron(sceneScheduleDto.getCron())
                    .build();
            sceneScheduleService.save(sceneSchedule);
            scheduleTask(sceneId, sceneSchedule);
        }
    }

    @Override
    public void triggerThree(Long sceneId, OperationEnum operationEnum) {
        //执行场景
        List<SceneDevice> sceneDeviceList = sceneDeviceService.getBySceneId(sceneId);
        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();
        for (SceneDevice sceneDevice : sceneDeviceList) {


            UserDevice userDevice = userDeviceService.getById(sceneDevice.getDeviceId());

            ThingModel thingModel = sceneDevice.getThingModel();
            handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder()
                    .userDevice(userDevice)
                    .delayedTime(sceneDevice.getDelayedTime())
                    .changeThingModel(thingModel)
                    .keyCode(sceneDevice.getKeyCode())
                    .build());

            log.info("trigger={}", userDevice.getIsTrigger());
        }
        asyncControl(handleUserDeviceDtoList, operationEnum);
    }

    @Override
    public void trigger(Long sceneId, OperationEnum operationEnum) {
        //执行场景
        List<SceneDevice> sceneDeviceList = sceneDeviceService.getBySceneId(sceneId);
        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();
        for (SceneDevice sceneDevice : sceneDeviceList) {


            UserDevice userDevice = userDeviceService.getById(sceneDevice.getDeviceId());

            ThingModel thingModel = sceneDevice.getThingModel();

            // 空调
            /*if ("airControl".equals(userDevice.getProductType())) {
                thingModel.getProperties().get(2).setValue(userDevice.getThingModel().getProperties().get(2).getValue());
            }*/

            log.info("场景执行空调thingModel={}", thingModel);

            userDevice.setIsTrigger(1);


            handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder()
                    .userDevice(userDevice)
                    .delayedTime(sceneDevice.getDelayedTime())
                    .changeThingModel(thingModel)
                    .keyCode(sceneDevice.getKeyCode())
                    .build());

            log.info("trigger={}", userDevice.getIsTrigger());
        }
        asyncControl(handleUserDeviceDtoList, operationEnum);
    }

    @Override
    public void triggerSceneCard(Long sceneId, OperationEnum operationEnum) {
        //执行场景
        List<SceneDevice> sceneDeviceList = sceneDeviceService.getBySceneId(sceneId);
        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();
        for (SceneDevice sceneDevice : sceneDeviceList) {


            UserDevice userDevice = userDeviceService.getById(sceneDevice.getDeviceId());

            if (!"lp_switch_three".equals(userDevice.getProductType()) && !"lp_switch_one".equals(userDevice.getProductType())
                    && !"lp_switch_two".equals(userDevice.getProductType()) && !"switch_one".equals(userDevice.getProductType())
                    && !"switch_three".equals(userDevice.getProductType()) && !"switch_two".equals(userDevice.getProductType())) {
                handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder()
                        .userDevice(userDevice)
                        .delayedTime(sceneDevice.getDelayedTime())
                        .changeThingModel(sceneDevice.getThingModel())
                        .keyCode(sceneDevice.getKeyCode())
                        .build());
            }
        }
        asyncControl(handleUserDeviceDtoList, operationEnum);
    }

    public void asyncControl(List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList, OperationEnum operationEnum) {
     /*   ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> {
            log.info("----------异步线程开始------------");
            bizUserDeviceService.handleList(handleUserDeviceDtoList, operationEnum);
            log.info("----------异步线程结束------------");
        });
        executorService.shutdown();//回收线程池*/

        bizUserDeviceService.handleList(handleUserDeviceDtoList, operationEnum);
    }


    @Override
    public void trigger(Long sceneId, String userId, OperationEnum operationEnum) {
        Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(sceneId)
                .userId(userId)
                .build()));
        scene.setLastExecutionTime(LocalDateTime.now());
        sceneService.updateById(scene);
        ValidUtils.isNullThrow(scene, "场景不存在");
        trigger(sceneId, operationEnum);
    }

    @Override
    public SceneDetailVo<SceneDevice, SceneSchedule> sceneInfo(Long sceneId) {

        Scene scene = sceneService.getById(sceneId);
        ValidUtils.isNullThrow(scene, "数据不存在");
        return sceneInfo(scene);
    }

    @Override
    public SceneDetailVo<SceneDevice, SceneSchedule> sceneInfo(Long sceneId, String userId) {
        Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(sceneId).userId(userId)
                .build()));
        ValidUtils.isNullThrow(scene, "数据不存在");
        return sceneInfo(scene);
    }

    @Override
    public void copy(SceneCopyDto dto, Long hotelId, String userId) {

        ValidUtils.isTrueThrow(dto.getHomeIds().size() == 0, "家庭ID不能为空");

        Scene templateScene = sceneService.getOne(new QueryWrapper<>(Scene.builder()
                .id(dto.getSceneId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(templateScene, "数据不存在");

        ValidUtils.isTrueThrow(dto.getHomeIds().contains(templateScene.getHomeId()), "不能给同一个家庭复制场景");

        //查询房间和酒店
        long count = hotelFloorHomeService.count(new QueryWrapper<>(HotelFloorHome
                .builder()
                .hotelId(hotelId)
                .build()).in("home_id", dto.getHomeIds()));
        ValidUtils.isFalseThrow(count == dto.getHomeIds().size(), "家庭ID有误");

        List<SceneDevice> templateSceneDeviceList = sceneDeviceService.list(new QueryWrapper<>(SceneDevice.builder()
                .sceneId(templateScene.getId())
                .build()));

        List<SceneSchedule> templateSceneScheduleList = sceneScheduleService.list(new QueryWrapper<>(SceneSchedule.builder()
                .sceneId(templateScene.getId())
                .build()));


        List<Long> errorHomeIds = new ArrayList<>();
        for (Long homeId : dto.getHomeIds()) {
            try {
                List<SceneDeviceDto> sceneDeviceList = copyMatchDevice(homeId, templateSceneDeviceList);
                List<SceneScheduleDto> sceneScheduleList = copySchedule(templateSceneScheduleList);

                //查询场景，名称匹配，存在则覆盖，不存在则新增
                List<Scene> sceneList = sceneService.list(new QueryWrapper<>(Scene.builder()
                        .sceneName(templateScene.getSceneName())
                        .homeId(homeId)
                        .build()));

                if (sceneList.size() == 0) {
                    //新增
                    this.add(SceneAddDto.builder()
                                    .homeId(homeId)
                                    .sceneName(templateScene.getSceneName())
                                    .command(templateScene.getCommand())
                                    .sceneIcon(templateScene.getSceneIcon())
                                    .sceneDevices(sceneDeviceList)
                                    .sceneScheduleList(sceneScheduleList)
                                    .build()
                            , userId, false);
                } else {
                    //编辑
                    this.edit(SceneEditDto.builder()
                                    .sceneId(sceneList.get(0).getId())
                                    .sceneName(templateScene.getSceneName())
                                    .command(templateScene.getCommand())
                                    .sceneIcon(templateScene.getSceneIcon())
                                    .sceneDevices(sceneDeviceList)
                                    .sceneScheduleList(sceneScheduleList)
                                    .build()
                            , userId, false);
                }
            } catch (Exception e) {
                log.error("BizSceneServiceImpl.copy", e);
                errorHomeIds.add(homeId);
            }

            if (errorHomeIds.size() != 0) {

                List<Home> homeList = homeService.list(new QueryWrapper<Home>()
                        .in("id", errorHomeIds));
                StringBuilder message = new StringBuilder();
                for (Home home : homeList) {
                    message.append("、").append(home.getHomeName());
                }
                throw CommonException.FAILURE("下列房间场景复制失败：" + message.substring(1));
            }
        }
    }

    @Override
    public List<OfflineSceneListVo> listScene(OfflineSceneDto dto) {
        UserDevice masterDevice = userDeviceService.findDeviceByDeviceIdAndRoomId(dto.getMasterDeviceId());
        ValidUtils.isNullThrow(masterDevice, "设备数据不存在");
        //获取场景数据
        List<Scene> scenes = sceneService.listByCondition(masterDevice.getHomeId(), dto.getSceneId());
        List<OfflineSceneListVo> offlineSceneListVos = new ArrayList<>();
        for (Scene it : scenes) {
            OfflineSceneListVo offlineSceneListVo = new OfflineSceneListVo();
            AuthSceneVo asv = new AuthSceneVo();
            BeanUtils.copyProperties(it, asv);
            offlineSceneListVo.setSceneVo(asv);
        }
        return offlineSceneListVos;
    }

    @Override
    public Scene findByLastAction(Long homeId) {
        return sceneService.findByLastAction(homeId);
    }

    private List<SceneScheduleDto> copySchedule(List<SceneSchedule> templateSceneScheduleList) {
        List<SceneScheduleDto> sceneScheduleList = new ArrayList<>();

        for (SceneSchedule sceneSchedule : templateSceneScheduleList) {
            sceneScheduleList.add(SceneScheduleDto.builder()
                    .enable(sceneSchedule.getEnable())
                    .cron(sceneSchedule.getCron())
                    .build());
        }
        return sceneScheduleList;
    }

    private List<SceneDeviceDto> copyMatchDevice(Long homeId, List<SceneDevice> templateSceneDeviceList) {

        List<SceneDeviceDto> sceneDeviceList = new ArrayList<>();

        //查看设备是否匹配
        for (SceneDevice templateSceneDevice : templateSceneDeviceList) {

            UserDevice templateUserDevice = userDeviceService.getById(templateSceneDevice.getDeviceId());

            List<UserDevice> deviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                    .homeId(homeId)
                    .customName(templateUserDevice.getCustomName())
                    .signalType(templateUserDevice.getSignalType())
                    .productType(templateUserDevice.getProductType())
                    .realProductType(templateUserDevice.getRealProductType())
                    .productId(templateUserDevice.getProductId())
                    .build()));

            //虚设备过滤一下属性值   因为同一个设备分裂的虚设备可能属性值不一样
            if (deviceList.size() > 1 && SignalEnum.INVENTED.getCode().equals(templateUserDevice.getSignalType())) {
                deviceList = deviceList.stream().filter(ud -> {

                    //设备的物理模型要一样
                    return ud.getThingModel().eqIdentifier(templateUserDevice.getThingModel());
                }).collect(Collectors.toList());
            }

            //过滤一下房间
            if (deviceList.size() > 1) {
                deviceList = deviceList.stream().filter(ud -> {

                    HomeRoom templateHomeRoom = homeRoomService.getById(templateUserDevice.getRoomId());
                    if (templateHomeRoom == null) {
                        return false;
                    }
                    List<HomeRoom> homeRoomList = homeRoomService.list(new QueryWrapper<>(HomeRoom.builder()
                            .homeId(homeId)
                            .roomName(templateHomeRoom.getRoomName())
                            .build()));
                    for (HomeRoom homeRoom : homeRoomList) {
                        if (ud.getRoomId().equals(homeRoom.getId())) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            ValidUtils.isFalseThrow(deviceList.size() == 1, "没有匹配到设备数据");
            UserDevice userDevice = deviceList.get(0);
            sceneDeviceList.add(SceneDeviceDto.builder()
                    .deviceId(userDevice.getDeviceId())
                    .thingModel(templateSceneDevice.getThingModel())
                    .build());
        }

        return sceneDeviceList;
    }

    private SceneDetailVo<SceneDevice, SceneSchedule> sceneInfo(Scene scene) {
        //获取场景执行设备数据
        List<SceneDevice> sceneDevices = sceneDeviceService.getBySceneId(scene.getId());

        for (SceneDevice sceneDevice : sceneDevices
        ) {

            try {
                if (sceneDevice.getThingModel() != null) {
                    String value = (String) sceneDevice.getThingModel().getProperties().get(0).getValue();

                    if ("0".equals(value)) {
                        sceneDevice.setModelStatus("关");
                    } else {
                        sceneDevice.setModelStatus("开");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, String> imageTypeMap = productTypeService.getMapData();

                                                                                                                                                                                                            Map<String, String> groupIdMap = userDeviceService.getGroupMapData(scene.getHomeId(), scene.getUserId());
        for (SceneDevice sd : sceneDevices) {
            sd.setImagesUrl(imageTypeMap.get(sd.getProductType()));
            sd.setGroupId(groupIdMap.get(sd.getDeviceId()));
            DeviceGroup one = iDeviceGroupService.getOne(new QueryWrapper<>(DeviceGroup.builder()
                    .deviceId(sd.getDeviceId()).groupId(sd.getGroupId()).build()));
            sd.setGroupName(one != null ? one.getGroupName() : null);
            Long modelId = userDeviceService.getById(sd.getDeviceId()).getModelId();
            sd.setModelId(modelId);
        }
        //获取场景执行定时数据
        List<SceneSchedule> sceneSchedules = sceneScheduleService.list(new QueryWrapper<>(SceneSchedule.builder()
                .sceneId(scene.getId()).build()));
        SceneDetailVo<SceneDevice, SceneSchedule> sdv = new SceneDetailVo<>();
        BeanUtils.copyProperties(scene, sdv);
        sdv.setSceneDeviceVos(sceneDevices);
        sdv.setSceneScheduleVos(sceneSchedules);

        //主控ID查找主控名称
        sdv.setMasterDeviceName("");
        if (StringUtils.isNotBlank(sdv.getMasterId())) {
            List<UserDevice> masterUserDeviceList = userDeviceService.list(new QueryWrapper<UserDevice>()
                    .in("device_id", sdv.getMasterId().split(",")));
            StringBuilder sb = new StringBuilder();
            for (UserDevice userDevice : masterUserDeviceList) {
                sb.append(",").append(userDevice.getCustomName());
            }
            sdv.setMasterDeviceName(sb.substring(1));
        }

        return sdv;
    }

    private void scheduleTask(Long sceneId, SceneSchedule sceneSchedule) {
        jobFeignClient.saveSceneJob(SceneJobParamDto.builder()
                .id(sceneId)
                .cron(sceneSchedule.getCron())
                .scheduleId(sceneSchedule.getId()).build());
    }

}
