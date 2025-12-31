package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeRoomAddDto;
import com.lj.iot.biz.base.dto.HomeRoomEditDto;
import com.lj.iot.biz.base.dto.RoomIdDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.db.smart.service.IHomeRoomService;
import com.lj.iot.biz.service.BizHomeRoomService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户家房间相关接口
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/home_room")
public class HomeRoomController {
    @Resource
    IHomeRoomService homeRoomService;
    @Autowired
    private BizHomeRoomService bizHomeRoomService;

    /**
     * 添加
     */
    @PostMapping("add")
    @CustomPermissions("home_room:add")
    public CommonResultVo<HomeRoom> add(@RequestBody @Valid HomeRoomAddDto dto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.add(dto.getHomeId(), dto.getRoomName(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 编辑
     *
     * @return
     */
    @PostMapping("edit")
    @CustomPermissions("home_room:edit")
    public CommonResultVo<HomeRoom> edit(@RequestBody @Valid HomeRoomEditDto dto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.edit(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 删除
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    @CustomPermissions("home_room:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid RoomIdDto dto) {
        bizHomeRoomService.delete(dto.getRoomId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询用户家房间列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    @CustomPermissions("home_room:list")
    public CommonResultVo<List<HomeRoom>> list(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(homeRoomService.customList(dto.getHomeId(), UserDto.getUser().getActualUserId()));
    }
}
