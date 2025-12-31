package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.service.BizRfModelService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 射频设备模型
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/rf_model")
public class AuthRFModelController {

    @Autowired
    private BizRfModelService bizRfModelService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("rf_model:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<RfModel>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizRfModelService.page(pageDto));
    }

    /**
     * 新增
     *
     * @return
     */
    @CustomPermissions("rf_model:add")
    @RequestMapping("/add")
    public CommonResultVo<String> add(@Valid RfModelAddDto paramDto) {
        bizRfModelService.add(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @return
     */
    @CustomPermissions("rf_model:edit")
    @RequestMapping("/edit")
    public CommonResultVo<String> edit(@Valid RfModelEditDto paramDto) {
        bizRfModelService.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @return
     */
    @CustomPermissions("rf_model:delete")
    @RequestMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto idDto) {
        bizRfModelService.delete(idDto);
        return CommonResultVo.SUCCESS();
    }


}
