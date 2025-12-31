package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.enums.NoticeTypeEnum;
import com.lj.iot.biz.base.vo.HotelCallVo;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.commom.vms.core.VMS;
import com.lj.iot.commom.vms.dto.VmsDto;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.dto.FutureDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.jpush.core.JPUSH;
import com.lj.iot.common.jpush.dto.Alert;
import com.lj.iot.common.jpush.dto.JPushDto;
import com.lj.iot.common.util.ValidUtils;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BizNoticeServiceImpl implements BizNoticeService {

    @Autowired
    private ICommunicateLogHisService communicateLogHisService;

    @Autowired
    private ISosContactService sosContactService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private BizNoticeService bizNoticeService;

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IUserGoodsService userGoodsService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Resource
    ISystemMessagesService systemMessagesService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Override
    public void callWaiter(UserDevice masterUserDevice, IntentDto intentDto) {

        Home home = homeService.getById(masterUserDevice.getHomeId());
        HomeRoom homeRoom = homeRoomService.getById(masterUserDevice.getRoomId());

        Notice notice = Notice.builder()
                .userId(masterUserDevice.getUserId())
                .homeId(home.getId())
                .roomId(homeRoom.getId())
                .deviceId(masterUserDevice.getDeviceId())
                .remarks(home.getHomeName() + "呼叫前台")
                .type(NoticeTypeEnum.CALL.getCode())
                .build();

        //查询是否是酒店用户
        HotelFloorHome hotelFloorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .homeId(home.getId())
                .build()));
        if (hotelFloorHome != null) {
            notice.setHotelId(hotelFloorHome.getHotelId());
        }

        //保存数据
        noticeService.save(notice);

        //ws推送数据
        bizWsPublishService.publish(WsResultVo.SUCCESS(masterUserDevice.getUserId(),
                masterUserDevice.getHomeId(),
                RedisTopicConstant.TOPIC_CALL,
                notice));
    }

    @Override
    public void order(UserDevice masterUserDevice, IntentDto intentDto) {

        Map<String, IntentDto.Slot> slotMap = intentDto.getSlots();

        //查询是否是酒店用户
        HotelFloorHome hotelFloorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .homeId(masterUserDevice.getHomeId())
                .build()));

        UserGoods userGoods = null;
        IntentDto.Slot goodsSlot = slotMap.get("goods");

        if (hotelFloorHome != null) {

            //酒店的商品都在一个家下，有可能命中同用户下其他酒店的商品
            List<UserGoods> userGoodsList = userGoodsService.list(new QueryWrapper<>(UserGoods.builder()
                    .userId(masterUserDevice.getUserId())
                    .hotelId(hotelFloorHome.getHotelId())
                    .state(true)
                    .build()));

            for (UserGoods goods : userGoodsList) {

                if (goods.getGoodsName().equals(goodsSlot.getValue())) {
                    userGoods = goods;
                    break;
                }
                if (StringUtils.isNotBlank(goods.getGoodsAlias())) {
                    for (String s : goods.getGoodsAlias().split(",")) {
                        if (s.equals(goodsSlot.getValue())) {
                            userGoods = goods;
                            break;
                        }
                    }
                    if (userGoods != null) {
                        break;
                    }
                }
            }

        } else {
            userGoods = userGoodsService.getOne(new QueryWrapper<>(UserGoods.builder()
                    .id(Long.parseLong(goodsSlot.getNormValue())) //改了aiui语料之后就失效了
                    //.goodsName(goodsSlot.getNormValue())
                    .userId(masterUserDevice.getUserId())
                    .state(true)
                    .build()));
        }

        ValidUtils.isNullThrow(userGoods, "商品不存在或已下架");


        Integer value = 1;
        IntentDto.Slot valueSlot = slotMap.get("value");
        if (valueSlot != null) {
            value = Integer.valueOf(valueSlot.getNormValue());
        }

        ValidUtils.isFalseThrow(userGoods.getQuantity() >= value, "库存不足");
        ValidUtils.isFalseThrow(value <= 20, "一次最多买20" + userGoods.getUnit() + userGoods.getGoodsName());

        Home home = homeService.getById(masterUserDevice.getHomeId());
        HomeRoom homeRoom = homeRoomService.getById(masterUserDevice.getRoomId());

        Notice notice = Notice.builder()
                .userId(masterUserDevice.getUserId())
                .deviceId(masterUserDevice.getDeviceId())
                .homeId(home.getId())
                .remarks(home.getHomeName() + "点了" + value + userGoods.getUnit() + userGoods.getGoodsName())
                .roomId(homeRoom.getId())
                .type(NoticeTypeEnum.CALL.getCode())
                .build();

        //查询是否是酒店用户
        if (hotelFloorHome != null) {
            notice.setHotelId(hotelFloorHome.getHotelId());
        }

        //保存数据
        noticeService.save(notice);

        //扣减库存
        userGoodsService.cutQuantity(userGoods.getId(), value);

        //ws推送数据
        bizWsPublishService.publish(WsResultVo.SUCCESS(masterUserDevice.getUserId(),
                masterUserDevice.getHomeId(),
                RedisTopicConstant.TOPIC_CALL,
                notice));
    }

    @Override
    public void sos(UserDevice masterUserDevice) {
        Home home = homeService.getById(masterUserDevice.getHomeId());
        HomeRoom homeRoom = homeRoomService.getById(masterUserDevice.getRoomId());

        Notice notice = Notice.builder()
                .userId(masterUserDevice.getUserId())
                .deviceId(masterUserDevice.getDeviceId())
                .homeId(home.getId())
                .remarks(home.getHomeName() + "呼救")
                .roomId(homeRoom.getId())
                .type(NoticeTypeEnum.SOS.getCode())
                .build();

        //查询是否是酒店用户
        HotelFloorHome hotelFloorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .homeId(home.getId())
                .build()));
        if (hotelFloorHome != null) {
            notice.setHotelId(hotelFloorHome.getHotelId());
        }

        //保存数据
        noticeService.save(notice);

        //ws推送数据
        bizWsPublishService.publish(WsResultVo.SUCCESS(masterUserDevice.getUserId(),
                masterUserDevice.getHomeId(),
                RedisTopicConstant.TOPIC_SOS,
                notice));
    }

    @Override
    public void handle(IdDto dto, String userId) {
        Notice notice = noticeService.getOne(new QueryWrapper<>(Notice.builder()
                .id(dto.getId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(notice, "数据不存在");
        ValidUtils.isTrueThrow(notice.getState(), "数据以处理");

        noticeService.updateById(Notice.builder()
                .id(notice.getId())
                .state(true)
                .build());
    }

    @Override
    public void forHelp(UserDevice userDevice, IntentDto intentDto) {
        if (userDevice == null) {
            log.error("AIUI.SKILL.forHelp:设备号不存在");
            return;
        }

        Home home = homeService.getById(userDevice.getHomeId());
        if (home == null) {
            log.error("AIUI.SKILL.forHelp:设备userDevice:{}没有对应的家", JSON.toJSONString(userDevice));
            return;
        }
        HomeRoom homeRoom = homeRoomService.getById(userDevice.getRoomId());
        if (homeRoom == null) {
            log.error("AIUI.SKILL.forHelp:设备userDevice:{}没有对应的房间数据", JSON.toJSONString(userDevice));
            return;
        }

        UserAccount userAccount = userAccountService.getByIdCache(userDevice.getUserId());
        if (userAccount == null) {
            log.error("AIUI.SKILL.forHelp:设备userDevice:{}没有对应的用户", JSON.toJSONString(userDevice));
            return;
        }

        //保存消息记录
        try {
            bizNoticeService.sos(userDevice);
        } catch (Exception e) {
            log.error("AIUI.SKILL.forHelp:保存呼救数据异常{}", JSON.toJSONString(userDevice));
        }

        //获取紧急联系人
        List<SosContact> sosContactList = sosContactService.list(new QueryWrapper<>(SosContact.builder()
                .homeId(userDevice.getHomeId()).build()));

        //没有紧急联系人,就给自己打电话
        //if (sosContactList.size()==0){
        SosContact build = SosContact.builder().homeId(home.getId()).phoneNumber(userAccount.getMobile()).username(userAccount.getNickname()).userId(userAccount.getId()).build();
        sosContactList.add(build);
        //}
        //电话呼救
        List<String> phoneNumberList = new ArrayList<>();
        for (SosContact sosContact : sosContactList) {
            phoneNumberList.add(sosContact.getPhoneNumber());
            /*VMS.async(VmsDto.builder()
                    .deviceName(userDevice.getCustomName())
                    .homeName(home.getHomeName())
                    .mobile(sosContact.getPhoneNumber())
                    .roomName(homeRoom.getRoomName())
                    .userName(userAccount.getNickname()).build(), future -> {
                FutureDto futureDto = (FutureDto) future.getNow();
                if (futureDto.isSuccess()) {
                    communicateLogHisService.save(CommunicateLogHis.builder()
                            .createDate(LocalDateTime.now())
                            .deviceName(userDevice.getDeviceName())
                            .homeId(home.getId())
                            .flag("1")
                            .userId(userDevice.getUserId())
                            .homeName(home.getHomeName())
                            .msg(futureDto.getMessage())
                            .code(futureDto.getCode())
                            .roomName(homeRoom.getRoomName())
                            .contactsName(userAccount.getNickname()).build());
                } else {
                    communicateLogHisService.save(CommunicateLogHis.builder()
                            .createDate(LocalDateTime.now())
                            .deviceName(userDevice.getDeviceName())
                            .homeId(home.getId())
                            .flag("0")
                            .userId(userDevice.getUserId())
                            .homeName(home.getHomeName())
                            .msg(futureDto.getMessage())
                            .code(futureDto.getCode())
                            .roomName(homeRoom.getRoomName())
                            .contactsName(userAccount.getNickname()).build());
                }
            });*/

            FutureDto futureDto = call((VmsDto.builder()
                    .deviceName(userDevice.getCustomName())
                    .homeName(home.getHomeName())
                    .mobile(sosContact.getPhoneNumber())
                    .roomName(homeRoom.getRoomName())
                    .masterDeviceId(userDevice.getMasterDeviceId())
                    .type(intentDto.getType())
                    .userName(userAccount.getNickname()).build()));
            intentDto.setCallMsg(futureDto.getMessage());
            if (futureDto.isSuccess()) {
                communicateLogHisService.save(CommunicateLogHis.builder()
                        .createDate(LocalDateTime.now())
                        .deviceName(userDevice.getDeviceName())
                        .homeId(home.getId())
                        .flag("1")
                        .userId(userDevice.getUserId())
                        .homeName(home.getHomeName())
                        .msg(futureDto.getMessage())
                        .code(futureDto.getCode())
                        .roomName(homeRoom.getRoomName())
                        .contactsName(userAccount.getNickname()).build());
            } else {
                communicateLogHisService.save(CommunicateLogHis.builder()
                        .createDate(LocalDateTime.now())
                        .deviceName(userDevice.getDeviceName())
                        .homeId(home.getId())
                        .flag("0")
                        .userId(userDevice.getUserId())
                        .homeName(home.getHomeName())
                        .msg(futureDto.getMessage())
                        .code(futureDto.getCode())
                        .roomName(homeRoom.getRoomName())
                        .contactsName(userAccount.getNickname()).build());
            }
        }
        for (SosContact sosContact : sosContactList) {
            SystemMessages systemMessages = SystemMessages.builder()
                    .type(3)
                    .readType(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .userId(userAccount.getId())
                    .homeId(home.getId().intValue())
                    .messages(sosContact.getUsername() + "" + sosContact.getPhoneNumber())
                    .inType("紧急求助")
                    .build();
            //插入APP消息中心
            systemMessagesService.save(systemMessages);
        }
        //极光推送
        final String text = home.getHomeName() + "用户通过设备\"" + userDevice.getCustomName() + "\"，向您发出求助信号";
        JPUSH.async(JPushDto.builder()
                .alias(phoneNumberList)
                .alert(Alert.builder()
                        .title("求助")
                        .body(text)
                        .msgType("sos").build()).build(), future -> {
        });
    }

    @Autowired
    private VmsProperties vmsProperties;

    public FutureDto call(VmsDto dto) {
        JSONObject params = new JSONObject();
        String message = "家庭" + dto.getHomeName() + (dto.getRoomName() == null ? "" : "房间" + dto.getRoomName()) + "的设备" + dto.getDeviceName();

        // 酒店特殊操作
        if ("hotel".equals(dto.getType())) {
            HotelCallVo dataVo = userDeviceService.getDeviceHotelCall(dto.getMasterDeviceId());
            message = dataVo.getFloorName() + dataVo.getHomeName() + "的语音网关面板";
        }
        params.put("deviceName", message);


        Config config = new Config()
                .setEndpoint(vmsProperties.getDomain())
                .setRegionId(vmsProperties.getRegionId())
                .setAccessKeyId(vmsProperties.getAccessKeyId())
                .setAccessKeyCCCFDF(vmsProperties.getAccessCCCFDF());
        Client client = null;
        try {
            client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final SingleCallByTtsRequest request = new SingleCallByTtsRequest()
                .setTtsCode(vmsProperties.getTtsCode())
                .setCalledNumber(dto.getMobile())
                .setCalledShowNumber(vmsProperties.getCalledShowNumber())
                .setTtsParam(JSON.toJSONString(params));
        try {
            log.info("VMS.call: {}", JSON.toJSONString(dto));
            final SingleCallByTtsResponse response = client.singleCallByTts(request);
            log.info("VMS.call: {}", JSON.toJSONString(response));


            return FutureDto.builder()
                    .body(dto)
                    .success("OK".equalsIgnoreCase(response.getBody().getCode()))
                    .code(response.getBody().getCode())
                    .message(response.getBody().getMessage())
                    .build();
        } catch (Exception e) {
            log.error("CALL.call呼叫失败", e);
            return FutureDto.builder()
                    .success(false)
                    .body(dto)
                    .code("-1")
                    .message(e.getMessage())
                    .build();
        }
    }
}
