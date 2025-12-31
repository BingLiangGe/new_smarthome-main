package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.SkillEntity;
import com.lj.iot.biz.db.smart.service.ISkillEntityService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 实体
 */
@RestController
@RequestMapping("/api/auth/skill_entity")
public class AuthSkillEntityController {

    @Autowired
    private ISkillEntityService skillEntityService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("skill:entity:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<SkillEntity>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(skillEntityService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid SkillEntityAddDto paramDto) {

        skillEntityService.save(SkillEntity.builder()
                .intentName(paramDto.getIntentName())
                .entityKey(paramDto.getEntityKey())
                .entityName(paramDto.getEntityName())
                .supportProductType(paramDto.getSupportProductType())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> edit(@Valid SkillEntityEditDto paramDto) {

        SkillEntity skillEntity = skillEntityService.getById(paramDto.getId());
        ValidUtils.isNullThrow(skillEntity, "数据不存在");

        skillEntityService.updateById(SkillEntity.builder()
                .id(paramDto.getId())
                .intentName(paramDto.getIntentName())
                .entityKey(paramDto.getEntityKey())
                .entityName(paramDto.getEntityName())
                .supportProductType(paramDto.getSupportProductType())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto paramDto) {

        ValidUtils.isNullThrow(skillEntityService.getById(paramDto.getId()), "数据不存在");
        skillEntityService.removeById(paramDto.getId());
        return CommonResultVo.SUCCESS();
    }
}
