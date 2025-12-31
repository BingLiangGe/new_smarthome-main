package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.EntityAliasAddDto;
import com.lj.iot.biz.base.dto.EntityAliasEditDto;
import com.lj.iot.biz.base.dto.EntityAliasPageDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 设备管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/entity_alias")
public class AuthEntityAliasController {

    @Autowired
    private IEntityAliasService entityAliasService;

    @Autowired
    private BizUploadEntityService bizUploadEntityService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("entity:alias:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<EntityAlias>> devicePage(EntityAliasPageDto pageDto) {
        return CommonResultVo.SUCCESS(entityAliasService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("entity:alias:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid EntityAliasAddDto paramDto) {

        EntityAlias entityAlias = EntityAlias.builder()
                .deviceType(paramDto.getDeviceType())
                .attrType(paramDto.getAttrType())
                .entityKey(paramDto.getEntityKey())
                .entityName(paramDto.getEntityName())
                .build();

        EntityAlias db = entityAliasService.getOne(new QueryWrapper<>(entityAlias));
        ValidUtils.noNullThrow(db, "数据已存在");
        entityAliasService.save(entityAlias);

        if ("room".equals(entityAlias.getAttrType())) {
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.RoomName);
        } else if ("device".equals(entityAlias.getAttrType())) {
            //清除缓存
            entityAliasService.deleteCache(entityAlias.getDeviceType());
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:type:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> productTypeEdit(@Valid EntityAliasEditDto paramDto) {

        EntityAlias entityAlias = EntityAlias.builder()
                .deviceType(paramDto.getDeviceType())
                .attrType(paramDto.getAttrType())
                .entityKey(paramDto.getEntityKey())
                .entityName(paramDto.getEntityName())
                .build();

        EntityAlias db = entityAliasService.getById(paramDto.getId());
        ValidUtils.isNullThrow(db, "数据不存在");

        entityAlias.setId(paramDto.getId());
        entityAliasService.updateById(entityAlias);

        if ("room".equals(entityAlias.getAttrType())) {
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.RoomName);
        } else if ("device".equals(entityAlias.getAttrType())) {
            //清除缓存
            entityAliasService.deleteCache(entityAlias.getDeviceType());
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:type:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> productTypeDel(@Valid IdDto paramDto) {
        EntityAlias db = entityAliasService.getById(paramDto.getId());
        ValidUtils.isNullThrow(db, "设备类型不存在");

        entityAliasService.removeById(paramDto.getId());

        if ("room".equals(db.getAttrType())) {
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.RoomName);
        } else if ("device".equals(db.getAttrType())) {
            //清除缓存
            entityAliasService.deleteCache(db.getDeviceType());
            bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        }

        return CommonResultVo.SUCCESS();
    }
}
