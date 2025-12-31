package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeRoomAddDto;
import com.lj.iot.biz.base.dto.HomeRoomEditDto;
import com.lj.iot.biz.base.dto.RoomIdDto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private IEntityAliasService entityAliasService;

    /**
     * 添加
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("add")
    public CommonResultVo<HomeRoom> add(@RequestBody @Valid HomeRoomAddDto dto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.add(dto.getHomeId(), dto.getRoomName(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 编辑
     *
     * @return
     */
    @HomeAuth(value = "roomId", type = HomeAuth.PermType.EDIT)
    @PostMapping("edit")
    public CommonResultVo<HomeRoom> edit(@RequestBody @Valid HomeRoomEditDto dto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.edit(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 删除
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "roomId", type = HomeAuth.PermType.EDIT)
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid RoomIdDto dto) {
        bizHomeRoomService.delete(dto.getRoomId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询用户家房间列表
     *
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("list")
    public CommonResultVo<List<HomeRoom>> list(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(homeRoomService.customList(dto.getHomeId()));
    }




    /**
     * 查询用户家房间和推荐家庭列表
     *
     * @return
     */
    //@HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("all_list")
    public CommonResultVo<ArrayList<HomeRoom>> allList(@Valid HomeIdDto dto) {
        //查询家庭下的房间
        List<HomeRoom> homeRooms = homeRoomService.customList(dto.getHomeId());


        List<EntityAlias> room = entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("room")
                .build()));

        for (int i = 0; i < room.size(); i++) {
            HomeRoom homeRoom = new HomeRoom();
            homeRoom.setRoomName(room.get(i).getEntityName());
            homeRooms.add(homeRoom);
        }


        Map<String, List<HomeRoom>> map = homeRooms.stream().collect(Collectors.groupingBy(HomeRoom::getRoomName));
        ArrayList<HomeRoom> arrayList = new ArrayList<>();
        for(String s : map.keySet()) {
            HomeRoom homeRoom = new HomeRoom();
            homeRoom.setRoomName(s);
            //不等于空就是当前家庭的
            HomeRoom homeRoom1 = map.get(s).get(0);
            if(homeRoom1!=null){
                homeRoom.setId(homeRoom1.getId());
                homeRoom.setRoomId(homeRoom1.getId());
                homeRoom.setUserId(homeRoom1.getUserId());
                homeRoom.setHomeId(homeRoom1.getHomeId());
                arrayList.add(homeRoom);
            }

        }

        return CommonResultVo.SUCCESS(arrayList);
    }
}
