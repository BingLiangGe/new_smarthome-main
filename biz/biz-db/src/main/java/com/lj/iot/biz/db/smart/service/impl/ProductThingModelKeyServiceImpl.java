package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.vo.ProductThingModelKeyVo;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.mapper.ProductThingModelKeyMapper;
import com.lj.iot.biz.db.smart.service.IProductThingModelKeyService;
import com.lj.iot.common.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List ;

/**
 * <p>
 * 产品物模型属性按键定义表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-12
 */
@DS("smart")
@Service
@Slf4j
public class ProductThingModelKeyServiceImpl extends ServiceImpl<ProductThingModelKeyMapper, ProductThingModelKey> implements IProductThingModelKeyService {

    @Override
    public ProductThingModelKey getModeKeyByProductIdAndKeyIdx(String productId, String keyIdx) {
        return this.baseMapper.getModeKeyByProductIdAndKeyIdx(productId,keyIdx);
    }

    @Override
    public IPage<ProductThingModelKey> customPage(ProductIdPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 同一类产品可能有多套按键，以modelId区分，modelId为0表示共用按键
     *
     * @param productId 产品ID
     * @param modelId   模型ID
     * @return 模型按键列表
     */
    @Override
    public List<ProductThingModelKey> keyList(String productId, Long modelId) {
        // 构建缓存键
        String cacheKey = "ProductThingModelKey:" + productId + "." + modelId;

        // 从缓存中获取数据
        List<ProductThingModelKey> cachedList = (List<ProductThingModelKey>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            log.info("Cache hit for keyList with cache key: {}", cacheKey);
            return cachedList;
        } else {
            log.info("Cache miss for keyList with cache key: {}", cacheKey);
        }

        // 查询数据库是否有特定模型按钮，如果没有则使用公共按钮
        long count = this.count(new QueryWrapper<>(ProductThingModelKey.builder()
                .productId(productId)
                .modelId(modelId)
                .build()));

        if (count == 0) {
            modelId = 0L;
        }

        // 查询数据库获取数据
        List<ProductThingModelKey> result = this.baseMapper.customList(productId, modelId);

        // 将数据存入缓存
        redisTemplate.opsForValue().set(cacheKey, result);

        log.info("Loaded keyList from the database and stored in cache with cache key: {}", cacheKey);

        return result;
    }


    /**
     * 获取指定产品、模型和按键码的物模型属性按键
     *
     * @param productId 产品ID
     * @param modelId   模型ID
     * @param keyCode   按键码
     * @return 物模型属性按键
     */
    @Override
    public ProductThingModelKey getProductThingModelKey(String productId, Long modelId, String keyCode) {
        // 构建缓存键
        String cacheKey = "ProductThingModelKey:" + productId + "." + modelId + "." + keyCode;

        // 从缓存中获取数据
        ProductThingModelKey cachedKey = (ProductThingModelKey) redisTemplate.opsForValue().get(cacheKey);
        if (cachedKey != null) {
            log.info("Cache hit for getProductThingModelKey with cache key: {}", cacheKey);
            return cachedKey;
        } else {
            log.info("Cache miss for getProductThingModelKey with cache key: {}", cacheKey);
        }

        // 查询数据库是否有特定模型按钮，如果没有则使用公共按钮
        long count = this.count(new QueryWrapper<>(ProductThingModelKey.builder()
                .productId(productId)
                .keyCode(keyCode)
                .modelId(modelId)
                .build()));

        if (count == 0) {
            modelId = 0L;
        }

        // 查询数据库获取数据
        List<ProductThingModelKey> productThingModelKeyList = this.list(new QueryWrapper<>(ProductThingModelKey.builder()
                .productId(productId)
                .modelId(modelId)
                .keyCode(keyCode)
                .build()));

        // 如果列表为空，返回null
        if (productThingModelKeyList.size() == 0) {
            log.info("No data found in the database for getProductThingModelKey with cache key: {}", cacheKey);
            return null;
        }

        // 获取第一个元素
        ProductThingModelKey result = productThingModelKeyList.get(0);

        // 将数据存入缓存
        redisTemplate.opsForValue().set(cacheKey, result);

        log.info("Loaded getProductThingModelKey from the database and stored in cache with cache key: {}", cacheKey);

        return result;
    }

    /**
     * 获取指定产品、模型、标识和值的物模型属性按键
     *
     * @param productId 产品ID
     * @param modelId   模型ID
     * @param identify  标识
     * @param value     值
     * @return 物模型属性按键
     */
    @Override
    public ProductThingModelKey getProductThingModelKey(String productId, Long modelId, String identify, Integer value) {
        // 构建缓存键
        String cacheKey = "ProductThingModelKey:" + productId + "." + modelId + "." + identify + "." + value;

        // 从缓存中获取数据
        ProductThingModelKey cachedKey = (ProductThingModelKey) redisTemplate.opsForValue().get(cacheKey);
        if (cachedKey != null) {
            log.info("Cache hit for getProductThingModelKey with cache key: {}", cacheKey);
            return cachedKey;
        } else {
            log.info("Cache miss for getProductThingModelKey with cache key: {}", cacheKey);
        }

        // 查询数据库是否有特定模型按钮，如果没有则使用公共按钮
        long count = this.count(new QueryWrapper<>(ProductThingModelKey.builder()
                .productId(productId)
                .modelId(modelId)
                .build()));

        if (count == 0) {
            modelId = 0L;
        }

        // 查询数据库获取数据
        List<ProductThingModelKey> productThingModelKeyList = this.list(new QueryWrapper<>(ProductThingModelKey.builder()
                .productId(productId)
                .modelId(modelId)
                .identifier(identify)
                .build()));

        // 如果列表为空，返回null
        if (productThingModelKeyList.size() == 0) {
            log.info("No data found in the database for getProductThingModelKey with cache key: {}", cacheKey);
            return null;
        }

        // 遍历列表，查找匹配值的元素
        for (ProductThingModelKey productThingModelKey : productThingModelKeyList) {
            if (value.equals(productThingModelKey.getStep())) {
                // 将数据存入缓存
                redisTemplate.opsForValue().set(cacheKey, productThingModelKey);
                log.info("Loaded getProductThingModelKey from the database and stored in cache with cache key: {}", cacheKey);
                return productThingModelKey;
            }
        }

        log.info("Cache miss for getProductThingModelKey with cache key: {}", cacheKey);
        return null;
    }
}
