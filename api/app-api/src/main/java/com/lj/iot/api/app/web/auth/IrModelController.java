package com.lj.iot.api.app.web.auth;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lj.iot.biz.base.dto.IrModelDto;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.entity.JDat;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.biz.db.smart.service.IProductThingModelKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.db.smart.service.JDatService;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.biz.service.impl.BizIrDataServiceImpl;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/api/auth/ir_model")
public class IrModelController {
    @Resource
    IIrModelService irModelService;
    @Resource
    JDatService jDatService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IProductThingModelKeyService productThingModelKeyService;

    /**
     * 查询红外设备型号列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<IrModel>> list(@Valid IrModelDto dto) {
        return CommonResultVo.SUCCESS(irModelService.list(new QueryWrapper<>(IrModel.builder()
                .deviceTypeId(dto.getDeviceTypeId())
                .brandId(dto.getBrandId())
                .build())));
    }

    /**
     * 根据红外码组id查对应码
     *
     * @param kfid
     * @return
     */
    @RequestMapping("getIrDatalist")
    public CommonResultVo<IPage<JDat>> list(PageDto pageDto, @RequestParam("kfid") String kfid,@RequestParam("deviceId") String deviceId) {
        QueryWrapper<JDat> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.likeRight("tags", kfid);


        IPage<JDat> page = jDatService.page(PageUtil.page(pageDto), objectQueryWrapper);

        UserDevice userDevice= userDeviceService.getById(deviceId);

        ValidUtils.isNullThrow(userDevice,"设备不存在");

        for (JDat jData : page.getRecords()
        ) {
            jData.setDats(BizIrDataServiceImpl.decode(JSONObject.parseObject(jData.getDats()).getString("irdata")));

            if ("IR".equals(userDevice.getSignalType()) && !"airControl".equals(userDevice.getProductType())){
                ProductThingModelKey productThingModelKey= productThingModelKeyService.getModeKeyByProductIdAndKeyIdx(userDevice.getProductId(),jData.getTags().split("_")[1]);

                if (productThingModelKey == null)
                    continue;
                jData.setIdentifier(productThingModelKey.getIdentifier());
                jData.setKeyCode(productThingModelKey.getKeyCode());
            }
        }
        return CommonResultVo.SUCCESS(page);
    }
}
