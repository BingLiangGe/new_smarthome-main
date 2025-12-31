package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.enums.MusicOrderStatusEnum;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IMusicOrderService;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.pay.wx.WeChatPayV3;
import com.lj.iot.common.pay.wx.WxAppPayVo;
import com.lj.iot.common.util.IPUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 音乐卡订单管理
 */
@RestController
@RequestMapping("/api/auth/music/order")
public class MusicOrderController {
    @Resource
    IMusicOrderService musicOrderService;

    @Resource
    private WeChatPayV3 weChatPayV3;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IMusicProductService musicProductService;

    @Value("${wx.pay.notify.music}")
    private String notifyUrl;


    /**
     * 检测此设备是否已经购买过音乐卡
     *
     * @return true 为已购买
     */
    @PostMapping("check")
    @CustomPermissions("music:order:check")
    public CommonResultVo<Boolean> check(@RequestBody DeviceIdDto dto) {
        long count = musicOrderService.count(new QueryWrapper<>(MusicOrder.builder()
                .deviceId(dto.getDeviceId())
                .state(MusicOrderStatusEnum.SUCCESS.getCode())
                .build()));
        return CommonResultVo.SUCCESS(count > 0);
    }

    /**
     * 购买音乐卡
     *
     * @param dto
     * @return
     */
    @PostMapping("add")
    @CustomPermissions("music:order:add")
    public CommonResultVo<WxAppPayVo> add(@RequestBody DeviceIdDto dto) {
        long count = musicOrderService.count(new QueryWrapper<>(MusicOrder.builder()
                .deviceId(dto.getDeviceId())
                .state(MusicOrderStatusEnum.SUCCESS.getCode())
                .build()));
        ValidUtils.isTrueThrow(count > 0, "此设备已经购买过音乐畅听卡");

        //查找设备
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(dto.getDeviceId())
                .build()));
        ValidUtils.isNullThrow(userDevice, "此设备不存在");


        MusicProduct musicProduct = musicProductService.list().get(0);

        //保存订单，并调用微信
        MusicOrder musicOrder = MusicOrder.builder()
                .amount(musicProduct.getPrice())
                .musicId(musicProduct.getId())
                .orderNo(IdUtils.hexId())
                .deviceId(userDevice.getDeviceId())
                .payType(1)
                .state(MusicOrderStatusEnum.UN_PAY.getCode())
                .userId(userDevice.getUserId())
                .build();

        //设置35分钟后超时
        long expireTimeMillis = System.currentTimeMillis() + (35 * 60 * 1000);
        String timeExpire = DateFormatUtils.format(expireTimeMillis, "yyyy-MM-dd'T'HH:mm:ssZZ");

        String prepayId = weChatPayV3.unifiedOrder(musicOrder.getOrderNo(), musicOrder.getAmount(), "购买音乐畅听卡"
                , musicOrder.getUserId(), notifyUrl, timeExpire, IPUtils.getIp());

        musicOrder.setPrepayId(prepayId);
        musicOrderService.save(musicOrder);

        return CommonResultVo.SUCCESS(weChatPayV3.getWxAppPayParams(prepayId));
    }
}
