package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.EntityAliasPageDto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xm
 * @since 2022-11-07
 */
public interface IEntityAliasService extends IService<EntityAlias> {

    IPage<EntityAlias> customPage(EntityAliasPageDto pageDto);

    List<EntityAlias> listCacheByProductType(String productType);

    void deleteCache(String productType);

    String getEntityKey(String type, String entityName);

    String getDeviceKey(String entityName);

    Boolean isExistDevice(String productType, String entityName);
}
