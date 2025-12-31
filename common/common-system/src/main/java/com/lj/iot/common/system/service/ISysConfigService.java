package com.lj.iot.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.system.dto.SysConfigAddDto;
import com.lj.iot.common.system.dto.SysConfigEditDto;
import com.lj.iot.common.system.entity.SysConfig;

/**
 * <p>
 * 系统配置信息表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface ISysConfigService extends IService<SysConfig> {

    void add(SysConfigAddDto dto);

    void edit(SysConfigEditDto dto);

    void deleteBatch(Long[] ids);

    /**
     * 根据key，获取配置的value值
     *
     * @param key key
     */
    public String getValue(String key);

    /**
     * 根据key，获取value的Object对象
     *
     * @param key   key
     * @param clazz Object对象
     */
    <T> T getConfigObject(String key, Class<T> clazz);


}
