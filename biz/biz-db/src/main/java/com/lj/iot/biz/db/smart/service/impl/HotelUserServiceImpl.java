package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.vo.HotelDataVo;
import com.lj.iot.biz.base.vo.HotelUserPageVo;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;
import com.lj.iot.biz.db.smart.mapper.HotelUserMapper;
import com.lj.iot.biz.db.smart.service.IHotelRoleMenuService;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 酒店和用户关联表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelUserServiceImpl extends ServiceImpl<HotelUserMapper, HotelUser> implements IHotelUserService {

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Autowired
    private IHotelRoleMenuService hotelRoleMenuService;

    @Override
    public List<String> permissions(Boolean isMain, Long hotelId, String hotelUserId) {

        if (isMain) {
            return hotelRoleMenuService.mainPermissions();
        }

        HotelUserRole hotelUserRole = hotelUserRoleService.getOne(new QueryWrapper<>(HotelUserRole.builder()
                .hotelId(hotelId)
                .hotelUserId(hotelUserId)
                .build()));
        ValidUtils.isNullThrow(hotelUserRole, "你还没有权限");

        return hotelRoleMenuService.permissions(hotelUserRole.getHotelId(), hotelUserRole.getRoleId());
    }

    @Override
    public List<HotelDataVo> listHotel(String hotelUserId) {
        return this.baseMapper.listHotel(hotelUserId);
    }

    @Override
    public HotelDataVo defaultHotel(String hotelUserId) {
        return this.baseMapper.defaultHotel(hotelUserId);
    }

    @Override
    public IPage<HotelUserPageVo> customPage(PageDto dto, Long hotelId) {
        IPage<HotelUserPageVo> page = this.baseMapper.customPage(PageUtil.page(dto), dto, hotelId);

        String loginAccount = UserDto.getUser().getAccount();

        for (int i = 0; i < page.getRecords().size(); i++) {
            HotelUserPageVo record = page.getRecords().get(i);

            if (loginAccount.equals(record.getNickname()) && loginAccount.equals(record.getMobile())) {
                page.getRecords().remove(i);
                continue;
            }

            HotelUserRole hotelUserRole = hotelUserRoleService.getOne(new QueryWrapper<>(HotelUserRole.builder()
                    .hotelUserId(record.getUserId())
                    .build()));

            if (hotelUserRole != null) {
                record.setRoleId(hotelUserRole.getRoleId());
            }
        }
        return page;
    }
}
