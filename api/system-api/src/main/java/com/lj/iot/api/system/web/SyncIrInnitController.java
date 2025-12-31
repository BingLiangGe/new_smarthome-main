package com.lj.iot.api.system.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.service.IIrBrandTypeService;
import com.lj.iot.biz.db.smart.service.IIrDataService;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.biz.service.BizIrDataService;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 同步数据
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@Slf4j
@RestController
@RequestMapping("api/open/ir_init")
public class SyncIrInnitController {
    @Autowired
    private IIrModelService irModelService;
    @Autowired
    private IIrBrandTypeService irBrandTypeService;

    @Autowired
    private BizIrDataService bizIrDataService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IIrDataService irDataService;

    // @GetMapping("sync_model")
    public CommonResultVo<String> syncModel() {


        List<IrBrandType> brandTypeList = irBrandTypeService.list();
        for (IrBrandType brandType : brandTypeList) {
            try {
               /* IrModel model = irModelService.getOne(new QueryWrapper<>(IrModel.builder()
                        .deviceTypeId(brandType.getDeviceTypeId())
                        .brandId(brandType.getId())
                        .build()));
                if (model != null) {
                    continue;
                }*/
                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("device_id", brandType.getDeviceTypeId() + "");
                map.put("brand_id", brandType.getBrandId() + "");
                Thread.sleep(500);
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getmodellist.php", map);
                JSONArray jsonArray = JSON.parseArray(result);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                    irModelService.save(IrModel.builder()
                            .deviceTypeId(brandType.getDeviceTypeId())
                            .brandId(brandType.getId())
                            .brandName(brandType.getBrandName())
                            .modeName(jsonObject.getString("bn"))
                            .fileId(jsonObject.getString("id"))
                            .build());
                }

            } catch (Exception e) {
                log.error("xxxxx", e);
            }
        }
        return CommonResultVo.SUCCESS();
    }


    @GetMapping("sync_data")
    public CommonResultVo<String> syncData() {
        List<IrModel> modelList = irModelService.list(new QueryWrapper<>(IrModel.builder()
                .deviceTypeId(1L)
                .build()));

        ThingModel thingModel = new ThingModel();
        List<ThingModelProperty> properties = new ArrayList<>();
        thingModel.setProperties(properties);
        for (IrModel model : modelList) {

            //powerstate
            for (String powerstate : Arrays.asList("0", "1")) {
                properties.add(ThingModelProperty.builder().identifier("powerstate").value(powerstate).build());

                //workmode
                for (String workmode : Arrays.asList("0", "1", "2", "3", "4")) {
                    properties.add(ThingModelProperty.builder().identifier("workmode").value(workmode).build());

                    //temperature
                    for (int i = 16; i <= 30; i++) {
                        properties.add(ThingModelProperty.builder().identifier("temperature").value(i).build());

                        //fanspeed
                        for (String fanspeed : Arrays.asList("0", "1", "2", "3")) {
                            properties.add(ThingModelProperty.builder().identifier("fanspeed").value(fanspeed).build());

                            //airdirection
                            for (String airdirection : Arrays.asList("0", "1", "2", "3", "4")) {
                                properties.add(ThingModelProperty.builder().identifier("airdirection").value(airdirection).build());

                                for (int j = 0; j <= 4; j++) {

                                    String dataIndex = bizIrDataService.getDataIndex(ProductTypeEnum.AC.getCode(), model, thingModel, j);

                                    String data = cacheService.get("IR_CODE:" + model.getFileId() + ":" + dataIndex);
                                    if (data != null) {
                                        continue;
                                    }

                                    //查询ir_data 查看是否有码   有码返回
                                    IrData irData = irDataService.getOne(new QueryWrapper<>(IrData.builder()
                                            .fileId(model.getFileId())
                                            .dataIndex(dataIndex)
                                            .build()), false);

                                    if (irData != null) {
                                        data = irData.getIrData();
                                        cacheService.addSeconds("IR_CODE:" + model.getFileId() + ":" + dataIndex, data, 300);
                                        continue;
                                    }

                                    //通过接口获取码[解码,保存,返回]
                                    data = bizIrDataService.syncData(model.getFileId(), dataIndex);
                                    if(data==null){
                                        continue;
                                    }

                                    irDataService.save(IrData.builder()
                                            .dataIndex(dataIndex)
                                            .fileId(model.getFileId())
                                            .irData(data)
                                            .build());


                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }

        return CommonResultVo.SUCCESS();
    }

}
