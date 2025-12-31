package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.HotelMenu;
import com.lj.iot.biz.db.smart.service.IHotelMenuService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单
 */
@RestController
@RequestMapping("api/auth/menu")
public class HotelMenuController {

    @Autowired
    private IHotelMenuService hotelMenuService;

    @Autowired
    private IHotelUserService hotelUserService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    public CommonResultVo<Map<String, Object>> nav() {
        UserDto userDto = UserDto.getUser();
        List<HotelMenu> menuList = hotelMenuService.nav(userDto.getIsMain(), userDto.getHotelId(), userDto.getUId());
        List<String> permissions = hotelUserService.permissions(userDto.getIsMain(), userDto.getHotelId(), userDto.getUId());
        Map<String, Object> map = new HashMap<>();
        map.put("menuList", menuList);
        map.put("permissions", permissions);
        return CommonResultVo.SUCCESS(map);
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/list")
    @CustomPermissions("menu:list")
    public CommonResultVo<List<HotelMenu>> list() {
        return CommonResultVo.SUCCESS(hotelMenuService.allMenu());
    }

    /**
     * 菜单信息
     */
    @GetMapping("/info")
    @CustomPermissions("menu:info")
    public CommonResultVo<HotelMenu> info(@Valid IdDto dto) {
        HotelMenu menu = hotelMenuService.getById(dto.getId());
        return CommonResultVo.SUCCESS(menu);
    }
}
