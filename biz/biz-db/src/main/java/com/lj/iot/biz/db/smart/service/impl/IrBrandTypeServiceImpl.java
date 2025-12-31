package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.vo.BrandTypeVo;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.lj.iot.biz.db.smart.mapper.IrBrandTypeMapper;
import com.lj.iot.biz.db.smart.service.IIrBrandTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
@DS("smart")
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class IrBrandTypeServiceImpl extends ServiceImpl<IrBrandTypeMapper, IrBrandType> implements IIrBrandTypeService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定设备类型ID的品牌类型列表
     *
     * @param deviceTypeId 设备类型ID
     * @return 品牌类型列表
     */
    @Override
    public List<BrandTypeVo> listByDeviceTypeId(Long deviceTypeId) {
        // 构建缓存键
        String cacheKey = "IrBrandTypeServiceImpl.listByDeviceTypeId:" + deviceTypeId;
        // 从缓存中获取数据
        List<BrandTypeVo> cachedList = (List<BrandTypeVo>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            log.info("Cache hit for listByDeviceTypeId with cache key: {}", cacheKey);
            return cachedList;
        } else {
            log.info("Cache miss for listByDeviceTypeId with cache key: {}", cacheKey);
        }
        // 查询数据库获取数据
        List<BrandTypeVo> result = this.baseMapper.listByDeviceTypeId(deviceTypeId);
        // 将数据存入缓存
        redisTemplate.opsForValue().set(cacheKey, result);
        log.info("Loaded listByDeviceTypeId from the database and stored in cache with cache key: {}", cacheKey);
        return result;
    }
}

