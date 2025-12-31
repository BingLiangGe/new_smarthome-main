package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.RfBrandAddDto;
import com.lj.iot.biz.base.dto.RfBrandEditDto;
import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.lj.iot.biz.service.BizRfBrandService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 射频品牌管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/rf_brand")
public class AuthRFBrandController {

    @Autowired
    private BizRfBrandService bizRfBrandService;

    /**
     * 射频品牌分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("rf_brand:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<RfBrand>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizRfBrandService.page(pageDto));
    }

    /**
     * 新增
     *
     * @return
     */
    @CustomPermissions("rf_brand:add")
    @RequestMapping("/add")
    public CommonResultVo<String> add(@Valid RfBrandAddDto paramDto) {
        bizRfBrandService.add(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @return
     */
    @CustomPermissions("rf_brand:edit")
    @RequestMapping("/edit")
    public CommonResultVo<String> edit(@Valid RfBrandEditDto paramDto) {
        bizRfBrandService.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @return
     */
    @CustomPermissions("rf_brand:delete")
    @RequestMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto idDto) {
        bizRfBrandService.delete(idDto);
        return CommonResultVo.SUCCESS();
    }


}
