package com.lj.iot.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdsDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.system.aop.SysLogAop;
import com.lj.iot.common.system.dto.SysConfigAddDto;
import com.lj.iot.common.system.dto.SysConfigEditDto;
import com.lj.iot.common.system.entity.SysConfig;
import com.lj.iot.common.system.service.ISysConfigService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统配置信息表
 *
 * @author xm
 * @since 2022-10-10
 */
@RestController
@RequestMapping("api/auth/sys/config")
public class SysConfigController {

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
    @CustomPermissions("sys:config:list")
    public CommonResultVo<IPage<SysConfig>> list(PageDto pageDto) {
        return CommonResultVo.SUCCESS(sysConfigService.page(PageUtil.page(pageDto),
                new QueryWrapper<>(SysConfig.builder().build())
                        .like("param_key", pageDto.getSearch())));
    }


    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    @CustomPermissions("sys:config:info")
    public CommonResultVo<SysConfig> info(@PathVariable("id") Long id) {
        SysConfig config = sysConfigService.getById(id);
        return CommonResultVo.SUCCESS(config);
    }

    /**
     * 保存配置
     */
    @SysLogAop("保存配置")
    @PostMapping("/save")
    @CustomPermissions("sys:config:save")
    public CommonResultVo<SysConfig> save(@Valid SysConfigAddDto dto) {

        sysConfigService.add(dto);

        return CommonResultVo.SUCCESS();
    }

    /**
     * 修改配置
     */
    @SysLogAop("修改配置")
    @PostMapping("/update")
    @CustomPermissions("sys:config:update")
    public CommonResultVo<SysConfig> update(@Valid SysConfigEditDto dto) {

        sysConfigService.edit(dto);

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除配置
     */
    @SysLogAop("删除配置")
    @PostMapping("/delete")
    @CustomPermissions("sys:config:delete")
    public CommonResultVo<SysConfig> delete(IdsDto dto) {
        Long[] ids=dto.getIds();
        sysConfigService.deleteBatch(ids);
        return CommonResultVo.SUCCESS();
    }

}
