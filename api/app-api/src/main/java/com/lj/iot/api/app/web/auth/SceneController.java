package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.SceneAddDto;
import com.lj.iot.biz.base.dto.SceneEditDto;
import com.lj.iot.biz.base.dto.SceneIdDto;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
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
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("list")
    public CommonResultVo<List<Scene>> list(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.authList(dto.getHomeId(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 创建场景
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("add")
    public CommonResultVo<Scene> add(@RequestBody @Valid SceneAddDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.add(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 修改场景
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "sceneId", type = HomeAuth.PermType.EDIT)
    @PostMapping("edit")
    public CommonResultVo<Scene> edit(@RequestBody @Valid SceneEditDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.edit(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 删除场景
     *
     * @param sceneIdDto
     * @return
     */
    @HomeAuth(value = "sceneId", type = HomeAuth.PermType.EDIT)
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid SceneIdDto sceneIdDto) {
        bizSceneService.deleteScene(sceneIdDto.getSceneId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 触发场景
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "sceneId", type = HomeAuth.PermType.ALL)
    @PostMapping("trigger")
    public CommonResultVo<String> trigger(@RequestBody @Valid SceneIdDto dto) {
        bizSceneService.trigger(dto.getSceneId(), OperationEnum.APP_C);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 获取场景详情
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "sceneId", type = HomeAuth.PermType.ALL)
    @RequestMapping("info")
    public CommonResultVo<SceneDetailVo<SceneDevice, SceneSchedule>> listBySceneId(@Valid SceneIdDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.sceneInfo(dto.getSceneId()));
    }

    /**
     * 获取场景图标列表
     */
    @RequestMapping("icon")
    public CommonResultVo<List<ScenePublicIcon>> ScenePublicIconList() {
        return CommonResultVo.SUCCESS(iScenePublicIconService.list());
    }

}
