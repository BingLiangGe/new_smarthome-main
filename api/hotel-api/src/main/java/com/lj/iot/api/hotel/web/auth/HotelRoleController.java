package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.api.hotel.aop.HotelLogAop;
import com.lj.iot.biz.base.dto.HotelRoleAddDto;
import com.lj.iot.biz.base.dto.HotelRoleEditDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.HotelRole;
import com.lj.iot.biz.db.smart.service.IHotelMenuService;
import com.lj.iot.biz.db.smart.service.IHotelRoleService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理
 */
@RestController
@RequestMapping("api/auth/sys/role")
public class HotelRoleController {

    @Autowired
    private IHotelRoleService hotelRoleService;

    @Autowired
    private IHotelMenuService hotelMenuService;

    /**
     * 角色列表
     *
     * @param pageDto
     * @return
     */
    @GetMapping("/page")
    @CustomPermissions("role:page")
    public CommonResultVo<IPage<HotelRole>> page(PageDto pageDto) {
        IPage<HotelRole> page = hotelRoleService.customPage(pageDto, UserDto.getUser().getHotelId());

        for (HotelRole record : page.getRecords()) {
            List<Long> menuIdList = hotelMenuService.queryMenuIdList(record.getRoleId());
            record.setMenuIdList(menuIdList);
        }
        return CommonResultVo.SUCCESS(page);
    }

    @GetMapping("/list")
    @CustomPermissions("role:list")
    public CommonResultVo<List<HotelRole>> list() {
        List<HotelRole> list = hotelRoleService.list(
                new QueryWrapper<>(HotelRole.builder()
                        .hotelId(UserDto.getUser().getHotelId())
                        .build()));
        return CommonResultVo.SUCCESS(list);
    }


    @GetMapping("/info")
    @CustomPermissions("role:info")
    public CommonResultVo<HotelRole> info(@Valid IdDto dto) {
        HotelRole role = hotelRoleService.getOne(
                new QueryWrapper<>(HotelRole.builder()
                        .roleId(dto.getId())
                        .hotelId(UserDto.getUser().getHotelId())
                        .build()));

        ValidUtils.isNullThrow(role, "数据不存在");
        //查询角色对应的菜单
        List<Long> menuIdList = hotelMenuService.queryMenuIdList(role.getRoleId());
        role.setMenuIdList(menuIdList);
        return CommonResultVo.SUCCESS(role);

    }

    @HotelLogAop("保存角色")
    @PostMapping("/add")
    @CustomPermissions("role:add")
    public CommonResultVo<HotelRole> save(@RequestBody @Valid HotelRoleAddDto dto) {
        UserDto userDto = UserDto.getUser();
        return CommonResultVo.SUCCESS(hotelRoleService.add(dto, userDto.getIsMain(), userDto.getHotelId(), userDto.getUId()));

    }

    @HotelLogAop("修改角色")
    @PostMapping("/edit")
    @CustomPermissions("role:edit")
    public CommonResultVo<HotelRole> edit(@RequestBody @Valid HotelRoleEditDto dto) {
        UserDto userDto = UserDto.getUser();
        return CommonResultVo.SUCCESS(hotelRoleService.edit(dto, userDto.getIsMain(), userDto.getHotelId(), userDto.getUId()));
    }

    @HotelLogAop("删除角色")
    @PostMapping("/delete")
    @CustomPermissions("role:delete")
    public CommonResultVo<String> delete(@RequestBody IdDto dto) {
        UserDto userDto = UserDto.getUser();
        hotelRoleService.delete(dto, userDto.getHotelId(), userDto.getUId());
        return CommonResultVo.SUCCESS();
    }
}
