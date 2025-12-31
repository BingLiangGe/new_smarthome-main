package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.FloorIdDto;
import com.lj.iot.biz.base.dto.HotelFloorAddDto;
import com.lj.iot.biz.base.dto.HotelFloorEditDto;
import com.lj.iot.biz.base.vo.FloorVo;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.service.BizHotelFloorService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 楼层
 */
@RestController
@RequestMapping("api/auth/hotel_floor")
public class HotelFloorController {

    @Autowired
    private BizHotelFloorService bizHotelFloorService;

    /**
     * 新增楼层
     */
    @CustomPermissions("hotel_floor:add")
    @PostMapping("add")
    public CommonResultVo<HotelFloor> add(@RequestBody @Valid HotelFloorAddDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorService.add(dto,
                UserDto.getUser().getHotelId(),
                UserDto.getUser().getActualUserId()));
    }

    /**
     * 编辑
     */
    @CustomPermissions("hotel_floor:edit")
    @PostMapping("edit")
    public CommonResultVo<HotelFloor> edit(@RequestBody @Valid HotelFloorEditDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorService.edit(dto,
                UserDto.getUser().getHotelId(),
                UserDto.getUser().getActualUserId()));
    }


    /**
     * 删除
     */
    @CustomPermissions("hotel_floor:delete")
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid FloorIdDto dto) {
        bizHotelFloorService.delete(dto, UserDto.getUser().getHotelId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * nav
     */
    @CustomPermissions("hotel_floor:nav")
    @PostMapping("nav")
    public CommonResultVo<List<FloorVo>> nav() {
        return CommonResultVo.SUCCESS(bizHotelFloorService
                .listFloorHomeVo(UserDto.getUser().getHotelId(),
                        UserDto.getUser().getActualUserId()));
    }



    /**
     * 查询楼梯
     * @return
     */
    @GetMapping("find_hotel_floor")
    public CommonResultVo<List<HotelFloor>> findHotelFloor() {
        return CommonResultVo.SUCCESS(bizHotelFloorService.listFloor(UserDto.getUser().getHotelId(),UserDto.getUser().getActualUserId()));
    }

    /**
     * 根据楼层ID查询房间
     * @return
     */
    @GetMapping("find_hotel_floor_room")
    public CommonResultVo<List<Map>> findHotelFloorRoom(@Valid FloorIdDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorService.findFloorRoom(dto.getFloorId(),UserDto.getUser().getActualUserId()));
    }
}
