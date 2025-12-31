package com.lj.iot.api.system.web.auth;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.service.BizAppUpgradeService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xm
 * @since 2023-02-23
 */
@RestController
@RequestMapping("/api/auth/appUpgrade")
public class AppUpgradeController {

    @Autowired
    private BizAppUpgradeService bizAppUpgradeService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IOperationLogService operationLogService;


    /**
     * app升级包标
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:upgrade:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<AppUpgrade>> productPage(ProductPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizAppUpgradeService.customPage(pageDto));
    }

    /**
     * 新增产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:add")
    @PostMapping("/add")
    public CommonResultVo<String> productAdd(@Valid AppUpgrade paramDto) {
        if (paramDto.getType()==1){
            //APP不需要下发
            bizAppUpgradeService.add(paramDto);

        }else {
            bizAppUpgradeService.add(paramDto);
            //3326下发指令更新
            List<Device> list = deviceService.list(new QueryWrapper<>(Device.builder()
                    .productId("1000000100")
                    .build()));
            for (int i = 0; i < list.size(); i++) {
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_APP, "1000000100", list.get(i).getId());
                MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                        .filepath(paramDto.getUrl())
                        .softwareversion(String.valueOf(paramDto.getVersionCode()))
                        .productId("1000000100")
                        .deviceId(list.get(i).getId())
                        .details(paramDto.getDetails())
                        .build();
                MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                /*operationLogService.save(OperationLog.builder()
                        .action(new Byte("0"))
                        .deviceId(list.get(i).getId())
                        .productId("1000000100")
                        .params(JSON.toJSONString(mqttOtaDto))
                        .remark(topic)
                        .build());*/
            }
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> productEdit(@Valid AppUpgrade paramDto) {
        bizAppUpgradeService.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> productDel(@Valid AppUpgrade paramDto) {
        bizAppUpgradeService.delete(paramDto);
        return CommonResultVo.SUCCESS();
    }

    public static void main(String[] args) {
        String a="{\"properties\":[{\"name\":\"电源\",\"value\":\"0\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"identifier\":\"powerstate\"},{\"name\":\"工作模式\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"制冷\",\"2\":\"除湿\",\"3\":\"送风\",\"4\":\"制热\"}},\"identifier\":\"workmode\"},{\"name\":\"当前温度\",\"value\":\"23\",\"dataType\":{\"type\":\"int\",\"specs\":{\"max\":\"30\",\"min\":\"16\",\"step\":\"1\"}},\"identifier\":\"temperature\"},{\"name\":\"风速\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"低速\",\"2\":\"中速\",\"3\":\"高速\"}},\"identifier\":\"fanspeed\"},{\"name\":\"风向\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"风向1\",\"2\":\"风向2\",\"3\":\"风向3\",\"4\":\"风向4\"}},\"identifier\":\"airdirection\"}]}";
        System.out.println(JSON.parseObject(a, ThingModel.class));
    }
}
