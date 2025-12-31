package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ParameterConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 参数配置表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
public interface IParameterConfigService extends IService<ParameterConfig> {

    IPage<ParameterConfig> customPage(PageDto pageDto);

    String getString(String key);

    Integer getInteger(String key);

    ParameterConfig getConfig(String key);

    Boolean getBoolean(String key);

    Long getLong(String key);

    BigDecimal getBigDecimal(String key);

    void edit(String key, String dictionaryValue);
}
