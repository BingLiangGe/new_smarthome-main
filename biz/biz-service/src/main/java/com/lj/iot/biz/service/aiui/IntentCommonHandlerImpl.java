package com.lj.iot.biz.service.aiui;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.UserDeviceFilterVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.aiui.enums.TurnOnOff;
import com.lj.iot.biz.service.enums.ModeEnum;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelDataType;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IntentCommonHandlerImpl implements IntentCommonHandler {

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private BizProductThingModelKeyService bizProductThingModelKeyService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IEntityAliasService entityAliasService;

    @Autowired
    private ISkillEntityService skillEntityService;

    @Autowired
    private ISkillEntityEntryService skillEntityEntryService;

    @Autowired
    private ICacheService cacheService;

    private Set<String> listProductType(String productTypes) {

        Set<String> productTypeIdSet = new HashSet<>();

        for (String productTypeStr : productTypes.split(",")) {

            ProductType productType = productTypeService.getCacheProductType(productTypeStr);
            if (productType == null) {
                continue;
            }

            List<ProductType> subProductTypeList = productTypeService.list(new QueryWrapper<>(ProductType.builder().build()).likeLeft("product_type_ray", productType.getProductTypeRay() + productType.getId() + "|"));

            for (ProductType type : subProductTypeList) {
                productTypeIdSet.add(type.getProductType());
            }
            productTypeIdSet.add(productType.getProductType());
        }
        return productTypeIdSet;
    }

    @Override
    public List<UserDeviceFilterVo> filter(UserDevice masterUserDevice, IntentDto intentDto, String productTypes) {

        List<UserDeviceFilterVo> userDeviceList;

        //明确说了房间，可以控制家庭下其他主控的设备
        IntentDto.Slot roomNameSlot = intentDto.getSlots().get("RoomName");
        if (roomNameSlot != null) {
            String roomName = roomNameSlot.getValue();
            Set<Long> roomIdSet = homeRoomService.setIdByHomeIdAndRoomName(masterUserDevice.getHomeId(), roomName);
            ValidUtils.isTrueThrow(roomIdSet.size() == 0, "没有找到对应房间");
            userDeviceList = userDeviceService.listBySetRoomId(roomIdSet);
        } else {
            //之前查主控下面的设备，现在改为查家下面的设备列表
            //再次改为查主控下的设备
            userDeviceList = userDeviceService.listByMasterDeviceId(intentDto.getMasterDeviceId());
            //userDeviceList = userDeviceService.listByHomeId(masterUserDevice.getHomeId());
        }

        if (StringUtils.isNotBlank(productTypes)) {
            Set<String> productTypeIdSet = listProductType(productTypes);
            userDeviceList = userDeviceList.stream().filter(it -> productTypeIdSet.contains(it.getProductType())).collect(Collectors.toList());
        }

        IntentDto.Slot deviceNameSlot = intentDto.getSlots().get("device");
        if (deviceNameSlot != null) {
            //所有
            IntentDto.Slot allSlot = intentDto.getSlots().get("range");
            if (allSlot != null) {
                userDeviceList = filterAll(userDeviceList, deviceNameSlot);
            } else {
                userDeviceList = filterNotAll(userDeviceList, deviceNameSlot, intentDto);
            }
        }

        if (userDeviceList.isEmpty()) {
            if ("打开所有灯".equals(intentDto.getText()) || "关闭所有灯".equals(intentDto.getText()) || "请把灯打开".equals(intentDto.getText())
                    || "请开灯".equals(intentDto.getText()) ||  "请帮我打开灯".equals(intentDto.getText()) || "把灯打开".equals(intentDto.getText())
                    || "请把灯打开".equals(intentDto.getText())  || "开灯".equals(intentDto.getText())  || "灯打开".equals(intentDto.getText())
                    || "打开灯".equals(intentDto.getText())  || "打开灯模式".equals(intentDto.getText())  || "开灯模式".equals(intentDto.getText())
                    || "请把灯关闭".equals(intentDto.getText())
                    || "请关灯".equals(intentDto.getText()) ||  "请帮我关闭灯".equals(intentDto.getText()) || "把灯关闭".equals(intentDto.getText())
                    || "请把灯关闭".equals(intentDto.getText())  || "关灯".equals(intentDto.getText())  || "灯关闭".equals(intentDto.getText())
                    || "关闭灯".equals(intentDto.getText())  || "关闭灯模式".equals(intentDto.getText())  || "关灯模式".equals(intentDto.getText())
                    || "请帮我开灯".equals(intentDto.getText())  || "请帮我关灯".equals(intentDto.getText())
                    || "帮我关闭灯".equals(intentDto.getText())  || "帮我打开灯".equals(intentDto.getText())
                    || "关闭电灯".equals(intentDto.getText())  || "打开电灯".equals(intentDto.getText())
                    || "帮我开灯".equals(intentDto.getText())  || "帮我关灯".equals(intentDto.getText())
                    || "帮我把灯打开".equals(intentDto.getText())  || "帮我把灯关闭".equals(intentDto.getText())
                    || "请把我打开所有灯".equals(intentDto.getText())  || "帮我把所有灯关掉".equals(intentDto.getText())
                    || "帮我把所有灯打开".equals(intentDto.getText())  || "帮我把所有灯关闭".equals(intentDto.getText())
                    || "帮我打开所有灯".equals(intentDto.getText())  || "帮我关闭所有灯".equals(intentDto.getText())
                    || "帮我开一下所有灯".equals(intentDto.getText())  || "帮我关一下所有灯".equals(intentDto.getText())
                    || "请把所有灯打开".equals(intentDto.getText())  || "请把所有灯关闭".equals(intentDto.getText())
                    || "把所有灯开一下".equals(intentDto.getText())  || "把所有灯关一下".equals(intentDto.getText())
                    || "帮我开一下所有灯".equals(intentDto.getText())  || "帮我关一下所有灯".equals(intentDto.getText())
                    || "开一下所有灯".equals(intentDto.getText())  || "关一下所有灯".equals(intentDto.getText())

            ) {
                userDeviceList = userDeviceService.listByMasterDeviceId(intentDto.getMasterDeviceId());
                userDeviceList = userDeviceList.stream().filter(it -> it.getCustomName().contains("灯")).collect(Collectors.toList());
            }

            String deviceName=intentDto.getSlots().get("device").getValue();
            // 阿拉伯_>中文互转
            if (StringUtils.isNotBlank(deviceName) && deviceName.matches(".*\\d.*")) {

                String newAlis = com.lj.iot.common.aiui.core.util.NumberUtil.convertToChineseNumber(deviceName);

                if (!newAlis.equals(intentDto.getText())) {
                    userDeviceList = userDeviceService.listByMasterDeviceId(intentDto.getMasterDeviceId());
                    userDeviceList = userDeviceList.stream().filter(it -> it.getCustomName().contains(newAlis)).collect(Collectors.toList());
                }
            }
        }


        ValidUtils.listIsEmptyThrow(userDeviceList, "没有绑定" + deviceNameSlot.getValue() + "，或者该设备不能做此操作");
//        ValidUtils.listIsEmptyThrow(userDeviceList, "" + deviceNameSlot.getValue() + "，不支持该意图");
        ValidUtils.isTrueThrow(userDeviceList.size() == 1 && !userDeviceList.get(0).getStatus(), deviceNameSlot.getValue() + "设备已离线");
        return userDeviceList;
    }

    private List<UserDeviceFilterVo> filterAll(List<UserDeviceFilterVo> userDeviceList, IntentDto.Slot deviceNameSlot) {
        List<UserDeviceFilterVo> matchCustomName = new ArrayList<>();
        for (UserDeviceFilterVo it : userDeviceList) {
            if (StringUtils.equalsIgnoreCase(it.getCustomName(), deviceNameSlot.getValue()) || StringUtils.equalsIgnoreCase(it.getDeviceName(), deviceNameSlot.getValue()) || entityAliasService.isExistDevice(it.getProductType(), deviceNameSlot.getValue()) || it.getProductType().contains(deviceNameSlot.getNormValue())) {
                matchCustomName.add(it);
            }
        }
        return matchCustomName;
    }

    private List<UserDeviceFilterVo> filterNotAll(List<UserDeviceFilterVo> userDeviceList, IntentDto.Slot deviceNameSlot, IntentDto intentDto) {
        //1、先匹配自定义名，如果自定义名没有匹配上。匹配设备别名，如果设备别名匹配上了多个，则提示
        List<UserDeviceFilterVo> matchCustomName = new ArrayList<>();
        for (UserDeviceFilterVo it : userDeviceList) {
            if (StringUtils.equalsIgnoreCase(it.getCustomName(), deviceNameSlot.getValue())) {
                matchCustomName.add(it);
            }
        }
        if (matchCustomName.size() != 0) {
            return matchCustomName;
        }

        List<UserDeviceFilterVo> matchDeviceName = new ArrayList<>();
       /* Set<String> set = new HashSet<>();
        for (UserDeviceFilterVo it : userDeviceList) {
            if (StringUtils.equalsIgnoreCase(it.getDeviceName(), deviceNameSlot.getValue())
                    || entityAliasService.isExistDevice(it.getProductType(), deviceNameSlot.getValue())) {
                matchDeviceName.add(it);
                set.add(it.getCustomName());
            }
        }

        //设备开关控制有特别提示
        if (set.size() > 1 && "deviceSwitch".equals(intentDto.getIntentName())) {
            TurnOnOff onOff = TurnOnOff.parse(intentDto.getSlots().get("insType").getNormValue());
            String customName = onOff.getValue() + "所有" + deviceNameSlot.getValue();
            for (String s : set) {
                customName = customName + "、或者" + onOff.getValue() + s;
            }
//            String template = "匹配到多个" + deviceNameSlot.getValue() + ",你可以说：" + customName;
            //不抛出多个灯，直接返回再说一次
            String template = "请再说一次";
            throw CommonException.INSTANCE(CommonCodeEnum.KEEP_SESSION.getCode(), MessageFormat.format(template, deviceNameSlot.getValue(), deviceNameSlot.getValue(), customName.substring(1)));
        }*/
        return matchDeviceName;
    }

    @Override
    public List<HandleUserDeviceDto<UserDevice>> buildHandleData(UserDevice masterUserDevice, IntentDto intentDto) {

        //设备开关控制加入，多次对话唤醒
        intentDto = interceptDeviceSwitch(intentDto);

        //查询意图插槽   通用模式：一个技能只处理一个插槽，有多个插槽的，单独创建类处理
        SkillEntity skillEntity = skillEntityService.getByIntentName(intentDto.getIntentName());
        ValidUtils.isNullThrow(skillEntity, "意图插槽不存在");

        //查询匹配设备[这里的设备主控可能不是接收语言的这个主控，因为控制房间设备的时候可以出现控制其他主控的设备]
        List<UserDeviceFilterVo> userDeviceList = filter(masterUserDevice, intentDto, skillEntity.getSupportProductType());

        String entryKey = "";
        IntentDto.Slot slot = intentDto.getSlots().get(skillEntity.getEntityKey());

        if (slot != null) {
            entryKey = slot.getNormValue();
        }

        SkillEntityEntry skillEntityEntry = getSkillEntityEntry(skillEntity, entryKey);


        Map<String, IntentDto.Slot> slots = intentDto.getSlots();

        Integer value = null;

        if (slots.containsKey("value")) {
            IntentDto.Slot valueSlot = slots.get("value");
            if (valueSlot != null && StringUtils.isNotBlank(valueSlot.getNormValue())) {
                value = NumberUtil.parseInt(valueSlot.getNormValue().replaceAll("%", ""));
            }
        }else if (slots.containsKey("degree")){
            IntentDto.Slot valueSlot = slots.get("degree");
            if (valueSlot != null && StringUtils.isNotBlank(valueSlot.getNormValue())) {
                value = NumberUtil.parseInt(valueSlot.getNormValue().replaceAll("%", ""));
            }
        } else if (slots.containsKey("max")) {
            IntentDto.Slot valueSlot = slots.get("max");
            if (valueSlot != null && StringUtils.isNotBlank(valueSlot.getNormValue())) {
                value = valueSlot.getNormValue().equalsIgnoreCase("min") ? 0 : 100;
            }
        }

        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();
        for (UserDeviceFilterVo userDeviceFilterVo : userDeviceList) {
            UserDevice userDevice = userDeviceService.getById(userDeviceFilterVo.getDeviceId());

            ProductThingModelKey productThingModelKey = null;

            //mesh窗帘打开一半，这个地方不能直接匹配打开关闭按钮，而是位置按钮
            if ("curtain".equals(userDevice.getProductType()) && SignalEnum.MESH.getCode().equals(userDevice.getSignalType()) && slots.get("value") != null) {
                productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, "position");
            } else if ("curtain".equals(userDevice.getProductType()) && SignalEnum.MESH.getCode().equals(userDevice.getSignalType()) && slots.get("degree") != null) {
                productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, "position");
            }else {
                productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, skillEntityEntry.getKeyCode());
            }

            //有些设备没有配置，说明不支持
            if (productThingModelKey == null) {
                log.info("IntentCommonHandlerImpl.handle:没有配置对应按钮{}", JSON.toJSONString(skillEntityEntry));
                continue;
            }
            ThingModel thingModel = buildThingModel(userDevice, skillEntityEntry, productThingModelKey, value);

            handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(thingModel).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).build());
        }
        return handleUserDeviceDtoList;
    }

    private SkillEntityEntry getSkillEntityEntry(SkillEntity skillEntity, String entryKey) {
        List<SkillEntityEntry> list = skillEntityEntryService.list(new QueryWrapper<>(SkillEntityEntry.builder().intentName(skillEntity.getIntentName()).entityKey(skillEntity.getEntityKey()).build()));

        for (SkillEntityEntry skillEntityEntry : list) {
            //******灯带快乐模式*******//
            if (skillEntityEntry.getIntentName().equals("deviceModel")) {
                return skillEntityEntry;
            }
            //******灯带快乐模式*******//
            if (skillEntityEntry.getEntryKey().equals(entryKey)) {
                return skillEntityEntry;
            }
            for (String name : skillEntityEntry.getEntryName().split(",")) {
                if (name.equals(entryKey)) {
                    return skillEntityEntry;
                }
            }
        }
        throw CommonException.FAILURE("我没有听懂，请再说一遍吧");
    }

    private IntentDto interceptDeviceSwitch(IntentDto intentDto) {

        String redisKey = "intent:deviceSwitch:" + intentDto.getMasterDeviceId();
        if ("deviceSwitch".equals(intentDto.getIntentName())) {
            cacheService.addSeconds(redisKey, intentDto, 30);
        }
        IntentDto cacheIntentDto = cacheService.get(redisKey);
        if (cacheIntentDto != null) {
            boolean flag = "otherDevice".equals(intentDto.getIntentName());
            if (!flag) {
                if ("CONTROL".equals(intentDto.getIntentName())) {
                    Map<String, IntentDto.Slot> slotMap = intentDto.getSlots();
                    IntentDto.Slot deviceSlot = slotMap.get("device");
                    IntentDto.Slot roomSlot = slotMap.get("room");

                    IntentDto.Slot insTypeSlot = slotMap.get("insType");
                    flag = slotMap.keySet().size() == (roomSlot == null ? 2 : 3) || (deviceSlot != null && insTypeSlot != null && "set".equals(insTypeSlot.getNormValue()));
                }
            }
            if (flag) {
                cacheIntentDto.getSlots().put("device", intentDto.getSlots().get("device"));
                if (intentDto.getSlots().get("room") != null) {
                    cacheIntentDto.getSlots().put("room", intentDto.getSlots().get("room"));
                }
                intentDto = cacheIntentDto;
                cacheService.addSeconds(redisKey, intentDto, 30);
            }
        }
        return intentDto;
    }

    @Async
    @Override
    public void doSend(List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList, OperationEnum operationEnum) {
        bizUserDeviceService.handleList(handleUserDeviceDtoList, operationEnum);
    }

    private ThingModel buildThingModel(UserDevice userDevice, SkillEntityEntry skillEntityEntry, ProductThingModelKey productThingModelKey, Integer value) {

        Product product = productService.getById(userDevice.getProductId());

        ThingModel productThingModel = product.getThingModel();
        ThingModel historyThingModel = userDevice.getThingModel();

        //物理模型属性List 2 Map
        Map<String, ThingModelProperty> productThingModelMap = productThingModel.thingModel2Map();

        Map<String, ThingModelProperty> thingModelPropertyMap = historyThingModel.thingModel2Map();

        ThingModelProperty skillEntityEntryThingModelProperty = skillEntityEntry.getThingModelProperty();

        String identifier = getIdentifier(userDevice, productThingModelKey.getIdentifier());
        ThingModelProperty thingModelProperty = productThingModelMap.get(identifier);
        ThingModelProperty historyThingModelProperty = thingModelPropertyMap.get(identifier);

        ThingModelDataType dataType = thingModelProperty.getDataType();
        String type = dataType.getType();

        //只有可调节范围的属性才读value值
        if (value == null || !"int".equals(type)) {
            value = productThingModelKey.getStep();
        }
        //如果配置了固定的属性值，就设置固定的值。如果没有配置固定的值，按一定的规则生成value
        if (skillEntityEntryThingModelProperty != null) {
            historyThingModelProperty.setValue(skillEntityEntryThingModelProperty.getValue());
        } else {


            ModeEnum modeEnum = ModeEnum.parse(productThingModelKey.getMode());

            switch (Objects.requireNonNull(modeEnum)) {
                case EQ: {
                    historyThingModelProperty.setValue(value);
                    break;
                }
                case LOOP:
                case ADD: {
                    historyThingModelProperty.setValue(NumberUtil.parseInt(historyThingModelProperty.getValue() + "") + value);
                    break;
                }
                case REDUCE: {

                    //Math.abs(value)  数据库中减的mode存的是负数。备注：开始存的是正数，为了使场景保存设备不传keCode参数,能用identify查到唯一数据，改成了负数
                    historyThingModelProperty.setValue(NumberUtil.parseInt(historyThingModelProperty.getValue() + "") - Math.abs(value));
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + modeEnum);
            }


            Map<String, String> specs = dataType.getSpecs();
            switch (type) {
                case "bool":
                case "enum": {
                    value = NumberUtil.parseInt(historyThingModelProperty.getValue() + "") % specs.size();
                    break;
                }
                case "int": {
                    int max = NumberUtil.parseInt(specs.get("max"));
                    int min = NumberUtil.parseInt(specs.get("min"));
                    value = Math.min(Math.max(NumberUtil.parseInt(historyThingModelProperty.getValue() + ""), min), max);
                    break;
                }
            }
            historyThingModelProperty.setValue(value);
        }

        //空调需要全属性
        if (ProductTypeEnum.AC.getCode().equals(userDevice.getRealProductType())) {
            return historyThingModel;
        }
        List<ThingModelProperty> properties = new ArrayList<>();
        properties.add(historyThingModelProperty);

        return ThingModel.builder().properties(properties).build();
    }

    /**
     * 多个相同的属性，用 _n  区别的，比如三键开关，有三个电源下标为  powerstate_1 powerstate_2 powerstate_3  这个地方就是通过前缀 powerstate  找到对应的属性
     *
     * @param userDevice
     * @param identifier
     * @return
     */
    private String getIdentifier(UserDevice userDevice, String identifier) {
        ThingModel thingModel = userDevice.getThingModel();
        List<ThingModelProperty> thingModelPropertyDtoList = thingModel.getProperties();

        for (ThingModelProperty thingModelPropertyDto : thingModelPropertyDtoList) {
            if (thingModelPropertyDto.getIdentifier().startsWith(identifier)) {
                return thingModelPropertyDto.getIdentifier();
            }
        }
        return null;
    }

}
