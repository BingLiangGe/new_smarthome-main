package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.SkillEntityEntry;
import com.lj.iot.biz.db.smart.service.ISkillEntityEntryService;
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
 * 实体词条
 */
@RestController
@RequestMapping("/api/auth/skill_entity_entry")
public class AuthSkillEntityEntryController {

    @Autowired
    private ISkillEntityEntryService skillEntityEntryService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("skill:entity_entry:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<SkillEntityEntry>> page(EntityEntryPageDto pageDto) {
        return CommonResultVo.SUCCESS(skillEntityEntryService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity_entry:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid SkillEntityEntryAddDto paramDto) {

        skillEntityEntryService.save(SkillEntityEntry.builder()
                .intentName(paramDto.getIntentName())
                .entityKey(paramDto.getEntityKey())
                .entryKey(paramDto.getEntryKey())
                .entryName(paramDto.getEntryName())
                .thingModelProperty(paramDto.getThingModelProperty())
                .keyCode(paramDto.getKeyCode())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity_entry:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> edit(@Valid SkillEntityEntryEditDto paramDto) {

        SkillEntityEntry skillEntityEntry = skillEntityEntryService.getById(paramDto.getId());
        ValidUtils.isNullThrow(skillEntityEntry, "数据不存在");

        skillEntityEntryService.updateById(SkillEntityEntry.builder()
                .id(paramDto.getId())
                .intentName(paramDto.getIntentName())
                .entityKey(paramDto.getEntityKey())
                .entryKey(paramDto.getEntryKey())
                .entryName(paramDto.getEntryName())
                .thingModelProperty(paramDto.getThingModelProperty())
                .keyCode(paramDto.getKeyCode())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("skill:entity_entry:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto paramDto) {

        ValidUtils.isNullThrow(skillEntityEntryService.getById(paramDto.getId()), "数据不存在");
        skillEntityEntryService.removeById(paramDto.getId());
        return CommonResultVo.SUCCESS();
    }
}
