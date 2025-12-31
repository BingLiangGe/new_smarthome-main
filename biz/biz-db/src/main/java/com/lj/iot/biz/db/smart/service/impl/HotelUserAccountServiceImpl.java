package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.HotelUserAccountAddDto;
import com.lj.iot.biz.base.dto.HotelUserAccountEditDto;
import com.lj.iot.biz.base.dto.UserIdDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.Hotel;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.mapper.HotelUserAccountMapper;
import com.lj.iot.biz.db.smart.service.IHotelRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.common.util.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 酒店用户账号表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelUserAccountServiceImpl extends ServiceImpl<HotelUserAccountMapper, HotelUserAccount> implements IHotelUserAccountService {

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Autowired
    private IHotelRoleService hotelRoleService;

    @Resource
    private HotelUserAccountMapper hotelUserAccountMapper;

    @Override
    public PageUtil<Hotel> getHotelLimit(Integer pageIndex, Integer pageSize, Hotel hotel) {
        PageUtil<Hotel> page = new PageUtil<Hotel>();

        page.setRows(hotelUserAccountMapper.getHotelLimit(pageIndex, pageSize, hotel));
        page.setTotal(hotelUserAccountMapper.getHotelLimitCount(hotel));
        return page;
    }

    @Override
    public PageUtil<HotelUserAccount> getHotelUserLimit(Integer pageIndex, Integer pageSize, HotelUserAccount userAccount) {
        PageUtil<HotelUserAccount> page = new PageUtil<>();

        page.setRows(hotelUserAccountMapper.getHotelUserLimit(pageIndex, pageSize, userAccount));
        page.setTotal(hotelUserAccountMapper.getHotelUserLimitCount(userAccount));
        return page;
    }

    @DSTransactional
    @Override
    public HotelUserAccount add(Boolean isMain, Long hotelId, String hotelUserId, String actualUserId, HotelUserAccountAddDto dto) {

        //创建子账号
        HotelUserAccount user = HotelUserAccount.builder()
                .id(IdUtils.nextId())
                .actualUserId(actualUserId)
                .mobile(dto.getAccount())
                .nickname(dto.getNickname())
                .type(AccountTypeEnum.HOTEL_SUB.getCode())
                .build();
        this.save(user);

        //加入酒店成员
        HotelUser hotelUser = HotelUser.builder()
                .hotelId(hotelId)
                .hotelUserId(actualUserId)
                .isMain(false)
                .isDefault(true)
                .memberUserId(user.getId())
                .build();
        hotelUserService.save(hotelUser);

        //角色权限校验
        hotelRoleService.checkPerms(dto.getRoleId(), isMain, hotelId, hotelUserId);
        //角色绑定
        hotelUserRoleService.saveOrUpdate(hotelId, user.getId(), dto.getRoleId());

        user = this.getById(user.getId());
        user.setRoleIdList(Collections.singletonList(dto.getRoleId()));
        return user;
    }

    @Override
    public HotelUserAccount edit(Boolean isMain, Long hotelId, String userId, String actualUserId, HotelUserAccountEditDto dto) {

        HotelUserAccount user = this.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .actualUserId(actualUserId)
                .id(dto.getId())
                .build()));
        ValidUtils.isNullThrow(user, "账号不存在");

        //主账号，只能主账号修改
        if (user.getActualUserId().equals(user.getId())) {
            ValidUtils.isFalseThrow(user.getId().equals(userId), "不能编辑主账号");
        }

        this.updateById(HotelUserAccount.builder()
                .id(user.getId())
                .nickname(dto.getNickname())
                .mobile(dto.getAccount())
                .build());

        //角色权限校验
        hotelRoleService.checkPerms(dto.getRoleId(), isMain, hotelId, user.getId());
        //角色绑定
        hotelUserRoleService.saveOrUpdate(hotelId, user.getId(), dto.getRoleId());

        return this.getById(user.getId());
    }

    @Override
    public HotelUserAccount delete(Long hotelId, String actualUserId, UserIdDto dto) {
        HotelUserAccount user = this.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .actualUserId(actualUserId)
                .id(dto.getUserId())
                .build()));
        ValidUtils.isNullThrow(user, "账号不存在");
        ValidUtils.isFalseThrow(AccountTypeEnum.HOTEL_SUB.getCode().equals(user.getType()), "只能删除子账号");

        this.removeById(user.getId());

        hotelUserRoleService.delete(hotelId, user.getId());

        return user;
    }
}
