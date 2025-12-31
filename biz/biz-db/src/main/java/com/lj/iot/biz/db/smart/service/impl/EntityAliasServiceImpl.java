package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.EntityAliasPageDto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.mapper.EntityAliasMapper;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-11-07
 */
@DS("smart")
@Service
public class EntityAliasServiceImpl extends ServiceImpl<EntityAliasMapper, EntityAlias> implements IEntityAliasService {

    @Autowired
    private IProductTypeService productTypeService;

    @Override
    public IPage<EntityAlias> customPage(EntityAliasPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Cacheable(value = "entity-alias", key = "#productType", unless = "#result == null")
    @Override
    public List<EntityAlias> listCacheByProductType(String productType) {
        return this.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("device")
                .deviceType(productType)
                .build()));
    }

    @CacheEvict(value = "entity-alias", key = "#productType")
    @Override
    public void deleteCache(String productType) {

    }

    @Override
    public String getEntityKey(String type, String entityName) {
        return getKey("entity", type, entityName);
    }

    @Override
    public String getDeviceKey(String entityName) {
        EntityAlias entityAlias = this.getOne(new QueryWrapper<>(EntityAlias.builder()
                .attrType("device")
                .entityName(entityName)
                .build()));
        if (entityAlias != null) {
            return entityAlias.getDeviceType();
        }
        return entityName;

    }

    @Override
    public Boolean isExistDevice(String productType, String entityName) {
        if(productType.equalsIgnoreCase("socket")){
            return false;
        }
        EntityAlias entityAlias = this.getOne(new QueryWrapper<>(EntityAlias.builder()
                .attrType("device")
                .subDeviceType(productType)
                .entityName(entityName)
                .build()));
        if (entityAlias == null) {
            return false;
        }
        //相等
        if(entityAlias.getSubDeviceType().equals(productType)){
            return true;
        }
        ProductType entityAliasProductType = productTypeService.getCacheProductType(entityAlias.getSubDeviceType());
        if (entityAliasProductType == null) {
            return false;
        }
        ProductType userDeviceProductType = productTypeService.getCacheProductType(productType);
        if (userDeviceProductType == null) {
            return false;
        }
        //下级关系
        return userDeviceProductType.getProductTypeRay().startsWith(entityAliasProductType.getProductTypeRay() + entityAliasProductType.getId() + "|");
    }

    private String getKey(String attr, String type, String entityName) {
        EntityAlias entityAlias = this.getOne(new QueryWrapper<>(EntityAlias.builder()
                .attrType(attr)
                .deviceType(type)
                .entityName(entityName)
                .build()));
        if (entityAlias != null) {
            return entityAlias.getEntityKey();
        }
        return entityName;
    }
}
