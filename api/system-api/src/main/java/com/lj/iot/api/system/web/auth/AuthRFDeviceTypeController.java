package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.RfDeviceType;
import com.lj.iot.biz.service.BizRfDeviceTypeService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 射频设备类型管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/rf_device_type")
public class AuthRFDeviceTypeController {

    @Autowired
    private BizRfDeviceTypeService bizRfDeviceTypeService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("rf_device_type:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<RfDeviceType>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizRfDeviceTypeService.page(pageDto));
    }

    /**
     * 列表(品牌ID)
     *
     * @param idDto
     * @return
     */
    @CustomPermissions("rf_device_type:list")
    @RequestMapping("/list")
    public CommonResultVo<List<RfDeviceType>> list(@Valid BrandIdDto idDto) {
        return CommonResultVo.SUCCESS(bizRfDeviceTypeService.listByBrandId(idDto.getBrandId()));
    }

    /**
     * 新增
     *
     * @return
     */
    @CustomPermissions("rf_device_type:add")
    @RequestMapping("/add")
    public CommonResultVo<String> add(@Valid RfDeviceTypeAddDto paramDto) {
        bizRfDeviceTypeService.add(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @return
     */
    @CustomPermissions("rf_device_type:edit")
    @RequestMapping("/edit")
    public CommonResultVo<String> edit(@Valid RfDeviceTypeEditDto paramDto) {
        bizRfDeviceTypeService.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @return
     */
    @CustomPermissions("rf_device_type:delete")
    @RequestMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto idDto) {
        bizRfDeviceTypeService.delete(idDto);
        return CommonResultVo.SUCCESS();
    }


}
