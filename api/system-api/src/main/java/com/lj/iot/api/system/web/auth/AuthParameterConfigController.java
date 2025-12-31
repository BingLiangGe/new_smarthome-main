package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ConfigEditDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ParameterConfig;
import com.lj.iot.biz.db.smart.service.IParameterConfigService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 参数配置表 前端控制器
 *
 * @author xm
 * @since 2022-08-27
 */
@Controller
@RequestMapping("/api/auth/config")
public class AuthParameterConfigController {

    @Autowired
    private IParameterConfigService parameterConfigService;

    /**
     * 列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("config:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ParameterConfig>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(parameterConfigService.customPage(pageDto));
    }


    /**
     * 编辑
     *
     * @param configEditDto
     * @return
     */
    @CustomPermissions("config:edit")
    @RequestMapping("/edit")
    public CommonResultVo<String> edit(@Valid ConfigEditDto configEditDto) {
        parameterConfigService.edit(configEditDto.getDictionaryKey(),configEditDto.getDictionaryValue());
        return CommonResultVo.SUCCESS();
    }
}
