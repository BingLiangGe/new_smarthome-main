package com.lj.iot.api.app.web.open;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.OfflineSceneListVo;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.ISceneService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import jodd.util.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 场景相关
 */
@RestController
@RequestMapping("/api/open/scene")
public class OpenSceneController {

    Map map = new HashMap() {{
        put("hui2 jia1 mo2 shi4", "回家模式");
        put("li2 jia1 mo2 shi4", "离家模式");
        put("guan1 ying3 mo2 shi4", "观影模式");
        put("shui4 mian2 mo2 shi4", "睡眠模式");
        put("qi3 chuang2 mo2 shi4", "起床模式");
        put("yue4 du2 mo2 shi4", "阅读模式");
        put("hui4 ke4 mo2 shi4", "会客模式");
        put("wen1 xin1 mo2 shi4", "温馨模式");
        put("xiu1 xian2 mo2 shi4", "休闲模式");
        put("lang4 man4 mo2 shi4", "浪漫模式");
        put("jiu4 can1 mo2 shi4", "就餐模式");
        put("yong4 can1 mo2 shi4", "用餐模式");
        put("you2 xi4 mo2 shi4", "游戏模式");
        put("yu2 le4 mo2 shi4", "娱乐模式");
    }};

    @Resource
    BizSceneService bizSceneService;
    @Resource
    IUserDeviceService userDeviceService;
    @Resource
    ISceneService sceneService;

    @Resource
    IUploadEntityService uploadEntityService;


    /**
     * 清除实体
     *
     * @return
     */
    @RequestMapping("/clearEntity")
    public CommonResultVo<String> clearEntity() {
        List<UploadEntityItemDto> uploadEntityItemList = Lists.newArrayList();

        uploadEntityItemList.add(UploadEntityItemDto.builder()
                .name("00000").build());


        uploadEntityService.uploadCustomLevelTrigger(UploadEntityDto.builder().entityList(uploadEntityItemList)
                .dynamicEntitiesName(DynamicEntitiesNameEnum.SceneCorpus.getCode())
                .userId("20231006103202762623128286310400").build());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 更换情景模式图片显示
     *
     * @return
     */
    @RequestMapping("/caseImgPath")
    public CommonResultVo<String> caseImgPath() {

        Map<String, String> sceneMap = new HashMap<>();

        sceneMap.put("起床模式", "http://47.100.238.205:8888/images/qcms.png");
        sceneMap.put("睡眠模式", "http://47.100.238.205:8888/images/smms.png");
        sceneMap.put("观影模式", "http://47.100.238.205:8888/images/gyms.png");
        sceneMap.put("会客模式", "http://47.100.238.205:8888/images/hkms.png");

        for (String key : sceneMap.keySet()
        ) {
            UpdateWrapper wrapper = new UpdateWrapper();

            wrapper.eq("scene_name", key);
            wrapper.set("scene_icon", sceneMap.get(key));
            sceneService.update(wrapper);
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("list")
    public CommonResultVo<List<OfflineSceneListVo>> list(@Valid OfflineSceneDto dto) {
        return CommonResultVo.SUCCESS(bizSceneService.listScene(dto));
    }

    /**
     * 列表
     *
     * @param
     * @return
     */
    @RequestMapping("trigger")
    public CommonResultVo<String> trigger(@RequestParam("masterId") String masterId, @RequestParam("keyWord") String keyWord) {
        String bool = "执行完成";
        //根据主控id来查用户
        UserDevice device = userDeviceService.getOne(new QueryWrapper<UserDevice>().eq("device_id", masterId));
        ValidUtils.isNullThrow(device, "设备数据不存在");
        List<Scene> list = bizSceneService.list(device.getHomeId());
        // List<Scene> list = sceneService.list(new QueryWrapper<Scene>().like("master_id",masterId).eq("home_id",device.getHomeId()));

        //根据语音文字来判断场景
        String sceneName = "";
        Set set = map.keySet();
        for (Object key :
                set) {
            if (keyWord.equals(key)) {
                sceneName = map.get(key).toString();
            }
        }

        Long sceneId = 0L;
        for (Scene scene :
                list) {
            if (scene.getSceneName().equals(sceneName)) {
                if (StringUtil.isEmpty(scene.getMasterId())) { //场景主控为空直接放行
                    sceneId = scene.getId();
                } else if (scene.getMasterId().contains(masterId)) {
                    sceneId = scene.getId();
                }
            }
        }
        if (sceneId == 0L) {
            ValidUtils.isTrueThrow(true, "该主控未绑定场景");
        }

        //执行找到的场景
        bizSceneService.trigger(sceneId, OperationEnum.AI_S_C);

        return CommonResultVo.SUCCESS(bool);
    }
}
