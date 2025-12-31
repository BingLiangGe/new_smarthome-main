package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.SceneDetailVo;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.SceneDevice;
import com.lj.iot.biz.db.smart.entity.ScenePublicIcon;
import com.lj.iot.biz.db.smart.entity.SceneSchedule;
import com.lj.iot.biz.db.smart.service.IScenePublicIconService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 场景相关
 */
@RestController
@RequestMapping("/api/auth/scene")
public class SceneController {

    @Resource
    BizSceneService bizSceneService;

    @Resource
    IScenePublicIconService iScenePublicIconService;

    /**
     * 列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    @CustomPermissions("scene:list")
    public CommonResultVo<List<Scene>> list(@Valid HomeIdDto dto) {
        Scene scene = bizSceneService.findByLastAction(dto.getHomeId());
        return CommonResultVo.HOTELSUCCESS(bizSceneService.list(dto.getHomeId(), UserDto.getUser().getActualUserId()),scene);
    }

    /**
     * 创建场景
     *
     * @param dto
     * @return
     */
    @PostMapping("add")
    @CustomPermissions("scene:add")
    public CommonResultVo<Scene> add(@RequestBody @Valid SceneAddDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.add(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 修改场景
     *
     * @param dto
     * @return
     */
    @PostMapping("edit")
//    @CustomPermissions("scene:edit")
    public CommonResultVo<Scene> edit(@RequestBody @Valid SceneEditDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.edit(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 删除场景8
     *
     * @param sceneIdDto
     * @return
     */
    @PostMapping("delete")
    @CustomPermissions("scene:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid SceneIdDto sceneIdDto) {
        bizSceneService.deleteScene(sceneIdDto.getSceneId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 获取场景图标列表
     */
    @RequestMapping("icon")
    public CommonResultVo<List<ScenePublicIcon>> ScenePublicIconList() {
        return CommonResultVo.SUCCESS(iScenePublicIconService.list());
    }

    /**
     * 触发场景
     *
     * @param dto
     * @return
     */
    @PostMapping("trigger")
    @CustomPermissions("scene:trigger")
    public CommonResultVo<String> trigger(@RequestBody @Valid SceneIdDto dto) {
        bizSceneService.trigger(dto.getSceneId(), UserDto.getUser().getActualUserId(), OperationEnum.APP_S_C);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 获取场景详情
     *
     * @param dto
     * @return
     */
    @RequestMapping("info")
    @CustomPermissions("scene:info")
    public CommonResultVo<SceneDetailVo<SceneDevice, SceneSchedule>> listBySceneId(@Valid SceneIdDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.sceneInfo(dto.getSceneId(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 复制场景
     * @param dto
     * @return
     */
    @PostMapping("copy")
    @CustomPermissions("scene:copy")
    public CommonResultVo<String> copy(@RequestBody @Valid SceneCopyDto dto) {
        bizSceneService.copy(dto, UserDto.getUser().getHotelId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }
}
