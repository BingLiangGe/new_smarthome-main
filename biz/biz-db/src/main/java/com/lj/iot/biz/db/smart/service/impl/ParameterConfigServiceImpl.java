package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ParameterConfig;
import com.lj.iot.biz.db.smart.mapper.ParameterConfigMapper;
import com.lj.iot.biz.db.smart.service.IParameterConfigService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
@DS("smart")
@Service
public class ParameterConfigServiceImpl extends ServiceImpl<ParameterConfigMapper, ParameterConfig> implements IParameterConfigService {

    @Override
    public ParameterConfig getConfig(String key) {
        return this.getOne(new QueryWrapper<>(ParameterConfig.builder().dictionaryKey(key).build()));
    }

    @Override
    public IPage<ParameterConfig> customPage(PageDto pageDto) {
        IPage<ParameterConfig> page = PageUtil.page(pageDto);
        List<ParameterConfig> list = this.baseMapper.customPage(page, pageDto);
        page.setRecords(list);
        return page;
    }

    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public String getString(String key) {
        ParameterConfig parameterConfig = this.getConfig(key);
        if (parameterConfig == null) {
            return null;
        }
        return parameterConfig.getDictionaryValue();
    }

    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public Integer getInteger(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value);
    }

    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public Boolean getBoolean(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public Long getLong(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public BigDecimal getBigDecimal(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    @CacheEvict(value = "param-config", key = "#key")
    @Override
    public void edit(String key, String dictionaryValue) {
        this.baseMapper.update(ParameterConfig.builder()
                        .dictionaryValue(dictionaryValue)
                        .build(),
                new QueryWrapper<>(ParameterConfig.builder()
                        .dictionaryKey(key)
                        .build()));
    }
}
