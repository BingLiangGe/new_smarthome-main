package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.SosContactAddDto;
import com.lj.iot.biz.base.dto.SosContactEditDto;
import com.lj.iot.biz.base.enums.ContactTypeEnum;
import com.lj.iot.biz.base.vo.SosContactVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.db.smart.entity.OfficialPhone;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.db.smart.entity.SystemMessages;
import com.lj.iot.biz.db.smart.service.IHomeService;
import com.lj.iot.biz.db.smart.service.IOfficialPhoneService;
import com.lj.iot.biz.db.smart.service.ISosContactService;
import com.lj.iot.biz.db.smart.service.ISystemMessagesService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 联系人控制器
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/sos_contact")
public class SosContactController {
    @Resource
    ISosContactService sosContactService;

    @Resource
    IHomeService homeService;

    @Autowired
    private IOfficialPhoneService officialPhoneService;


    /**
     * 官方电话
     *
     * @return
     */
    @RequestMapping("official_phone")
    public CommonResultVo<List<OfficialPhone>> officialPhone() {
        return CommonResultVo.SUCCESS(officialPhoneService.list());
    }

    /**
     * 列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("list")
    public CommonResultVo<List<SosContactVo<SosContact>>> list(HomeIdDto dto) {
        List<SosContact> list = sosContactService.list(new QueryWrapper<>(SosContact.builder().homeId(dto.getHomeId()).build()));

        List<SosContact> property = new ArrayList<>();
        List<SosContact> relatives = new ArrayList<>();

        for (SosContact sosContact : list) {
            if (sosContact.getContactType().equals(ContactTypeEnum.property.getCode())) {
                property.add(sosContact);
                continue;
            }
            relatives.add(sosContact);
        }

        List<SosContactVo<SosContact>> sosContactList = new ArrayList<>();
        sosContactList.add(SosContactVo.<SosContact>builder()
                .type(ContactTypeEnum.property.getCode())
                .data(property)
                .build());
        sosContactList.add(SosContactVo.<SosContact>builder()
                .type(ContactTypeEnum.relatives.getCode())
                .data(relatives)
                .build());

        return CommonResultVo.SUCCESS(sosContactList);
    }

    /**
     * 新增电话
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.MAIN)
    @PostMapping("add")
    public CommonResultVo<SosContact> add(@RequestBody @Valid SosContactAddDto dto) {
        ContactTypeEnum contactTypeEnum = ContactTypeEnum.parse(dto.getContactType());
        ValidUtils.isNullThrow(contactTypeEnum, "类型不正确");

        Home home = homeService.getOne(new QueryWrapper<>(Home.builder().id(dto.getHomeId()).userId(UserDto.getUser().getUId()).build()));
        ValidUtils.isNullThrow(home, "数据不存在");
        SosContact sosContact = SosContact.builder()
                .userId(UserDto.getUser().getUId())
                .homeId(home.getId())
                .contactType(contactTypeEnum.getCode())
                .phoneNumber(dto.getPhoneNumber())
                .username(dto.getUsername()).build();
        sosContactService.save(sosContact);
        return CommonResultVo.SUCCESS(sosContactService.getById(sosContact.getId()));
    }

    /**
     * 编辑电话
     *
     * @param dto
     * @return
     */
    @PostMapping("edit")
    public CommonResultVo<SosContact> edit(@RequestBody @Valid SosContactEditDto dto) {

        SosContact sosContact = sosContactService.getOne(new QueryWrapper<>(SosContact.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getUId())
                .build()));
        ValidUtils.isNullThrow(sosContact, "数据不存在");
        sosContactService.updateById(SosContact.builder().id(sosContact.getId()).phoneNumber(dto.getPhoneNumber()).username(dto.getUsername()).build());
        return CommonResultVo.SUCCESS(sosContactService.getById(sosContact.getId()));
    }

    /**
     * 删除
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid IdDto dto) {
        SosContact sosContact = sosContactService.getOne(new QueryWrapper<>(SosContact.builder().id(dto.getId()).userId(UserDto.getUser().getUId()).build()));
        ValidUtils.isNullThrow(sosContact, "数据不存在");
        sosContactService.removeById(sosContact.getId());
        return CommonResultVo.SUCCESS();
    }

}
