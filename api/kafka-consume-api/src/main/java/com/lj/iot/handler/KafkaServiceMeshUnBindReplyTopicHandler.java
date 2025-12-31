package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.DeviceDto;
import com.lj.iot.biz.base.vo.UnBindMeshDeviceVo;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 配网状态蓝牙设备列表
 *
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaServiceMeshUnBindReplyTopicHandler extends AbstractTopicHandler {

    public KafkaServiceMeshUnBindReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_MESH_UNBIND_REPLY);
    }

    private Integer ZERO = Integer.parseInt("0");

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private IProductService productService;

    /**
     * 配网状态蓝牙设备列表
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
    @Override
    public void handle(HandleMessage message) {
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        JSONObject body = message.getBody();
        if (ZERO.compareTo(body.getInteger("code")) != 0) {
            log.error("ServiceMeshUnBindReplyTopicHandler.handle:互联设备绑定失败");
            bizWsPublishService.publish(WsResultVo.FAILURE(userDevice.getUserId(),
                    userDevice.getHomeId(),
                    RedisTopicConstant.TOPIC_MESH_UNBIND_LIST,
                    body.get("data")
            ));
            return;
        }

        List<DeviceDto> deviceDtoList = JSON.parseArray(body.getString("data"), DeviceDto.class);

        List<UnBindMeshDeviceVo> list = new ArrayList<>();
        for (DeviceDto deviceDto : deviceDtoList) {


            if (userDeviceService.count(new QueryWrapper<>(UserDevice.builder()
                    .deviceId(deviceDto.getDeviceId())
                    .build())) == 0) {

                Device device = deviceService.getOne(new QueryWrapper<>(Device.builder()
                        .id(deviceDto.getDeviceId())
                        .productId(deviceDto.getProductId())
                        .build()));
                if (device == null) {
                    continue;
                }
                Product product = productService.getById(device.getProductId());
                if (product == null) {
                    continue;
                }

                list.add(UnBindMeshDeviceVo.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .productCode(product.getProductCode())
                        .productType(product.getProductType())
                        .imagesUrl(product.getImagesUrl())
                        .deviceId(device.getId())
                        .build());
            }
        }

        bizWsPublishService.publish(WsResultVo.SUCCESS(userDevice.getUserId(),
                userDevice.getHomeId(),
                RedisTopicConstant.TOPIC_MESH_UNBIND_LIST,
                list
        ));
    }
}
