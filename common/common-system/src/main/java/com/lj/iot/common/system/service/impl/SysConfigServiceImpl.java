package com.lj.iot.common.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.system.dto.SysConfigAddDto;
import com.lj.iot.common.system.dto.SysConfigEditDto;
import com.lj.iot.common.system.entity.SysConfig;
import com.lj.iot.common.system.mapper.SysConfigMapper;
import com.lj.iot.common.system.service.ISysConfigService;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>
 * 系统配置信息表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
@DS("system")
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Override
    public void add(SysConfigAddDto dto) {
        this.save(SysConfig.builder()
                .paramKey(dto.getParamKey())
                .paramValue(dto.getParamValue())
                .remark(dto.getRemark())
                .build());
    }

    @Override
    public void edit(SysConfigEditDto dto) {
        SysConfig sysConfig = this.getById(dto.getId());
        ValidUtils.isNullThrow(sysConfig, "数据不存在");
        this.updateById(SysConfig.builder()
                .id(dto.getId())
                .paramKey(dto.getParamKey())
                .paramValue(dto.getParamValue())
                .remark(dto.getRemark())
                .build());
    }

    @Override
    public void deleteBatch(Long[] ids) {
        this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public String getValue(String key) {
        SysConfig sysConfig = this.getOne(new QueryWrapper<>(SysConfig.builder()
                .paramKey(key)
                .build()));
        ValidUtils.isNullThrow(sysConfig, "数据不存在");
        return sysConfig.getParamValue();
    }

    @Override
    public <T> T getConfigObject(String key, Class<T> clazz) {
        String value = getValue(key);
        ValidUtils.isNullThrow(value, "数据不存在");
        return JSON.parseObject(value, clazz);
    }
}
