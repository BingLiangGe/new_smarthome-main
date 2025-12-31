package com.lj.iot.api.app.web.auth;

import com.lj.iot.biz.db.smart.service.IHomeRoomService;
import com.lj.iot.biz.db.smart.service.ISceneTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 情景模板 前端控制器
 * </p>
 *
 * @author xm
 * @since 2023-03-08
 */
@RestController
@RequestMapping("/smart/sceneTemplate")
public class SceneTemplateController {
    @Resource
    ISceneTemplateService sceneTemplateService;
}
