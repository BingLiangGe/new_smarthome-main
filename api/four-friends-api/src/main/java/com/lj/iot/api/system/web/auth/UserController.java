package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HotelRoleAddDto;
import com.lj.iot.biz.base.dto.HotelUserAccountAddDto;
import com.lj.iot.biz.base.vo.HotelUserPageVo;
import com.lj.iot.biz.db.smart.entity.HotelMenu;
import com.lj.iot.biz.db.smart.entity.HotelRole;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.service.IHotelMenuService;
import com.lj.iot.biz.db.smart.service.IHotelRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/api/auth/user")
@RestController
public class UserController {

    @Autowired
    private IHotelRoleService hotelRoleService;

    @Autowired
    private IHotelMenuService hotelMenuService;


    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;



    /**
     * 新增用户
     */
    @PostMapping("/userAdd")
    //@CustomPermissions("hotel_user:add")
    public CommonResultVo<HotelUserAccount> add(@RequestBody @Valid HotelUserAccountAddDto dto) {
        UserDto userDto = UserDto.getUser();
        HotelUserAccount userAccount = hotelUserAccountService.add(
                userDto.getIsMain(),
                dto.getHotelId(),
                userDto.getUId(),
                userDto.getActualUserId(),
                dto);
        return CommonResultVo.SUCCESS(userAccount);
    }


    /**
     * 酒店用户列表
     */
    @GetMapping("/userPage")
    //@CustomPermissions("hotel_user:page")
    public CommonResultVo<IPage<HotelUserPageVo>> list(PageDto dto, Long hotelId) {
        return CommonResultVo.SUCCESS(hotelUserService.customPage(dto, hotelId));
    }


    /**
     * 保存角色
     *
     * @param dto
     * @return
     */
    @PostMapping("/roleAdd")
    //@CustomPermissions("role:add")
    public CommonResultVo<HotelRole> save(@RequestBody @Valid HotelRoleAddDto dto) {
        UserDto userDto = UserDto.getUser();
        return hotelRoleService.addParent(dto, userDto.getIsMain(), dto.getHotelId(), userDto.getUId());

    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/menuList")
    //@CustomPermissions("menu:list")
    public CommonResultVo<List<HotelMenu>> list() {
        return CommonResultVo.SUCCESS(hotelMenuService.allMenu());
    }

    @GetMapping("/rolePage")
    //@CustomPermissions("role:page")
    public CommonResultVo<IPage<HotelRole>> page(PageDto pageDto, Long hotelId) {
        IPage<HotelRole> page = hotelRoleService.customPage(pageDto, hotelId);

        for (HotelRole record : page.getRecords()) {
            List<Long> menuIdList = hotelMenuService.queryMenuIdList(record.getRoleId());
            record.setMenuIdList(menuIdList);
        }
        return CommonResultVo.SUCCESS(page);
    }

}
