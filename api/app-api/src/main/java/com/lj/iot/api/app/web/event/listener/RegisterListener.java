package com.lj.iot.api.app.web.event.listener;

import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeService;
import com.lj.iot.biz.service.BizUploadEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RegisterListener {
    @Resource
    IHomeService homeService;

    @Resource
    IHomeUserService homeUserService;

    @Resource
    BizUploadEntityService bizUploadEntityService;

    @Resource
    IHomeRoomService homeRoomService;

    @Autowired
    private BizHomeService bizHomeService;

    @Resource
    ISceneTemplateService sceneTemplateService;
    @Resource
    ISceneService sceneService;

    @EventListener
    public void listener(RegisterEvent event) {
        UserAccount userAccount = (UserAccount) event.getSource();
        Home home = Home.builder()
                .userId(userAccount.getId())
                .isDefaultHome(true)
                .homeName("我的家")
                .build();
        homeService.save(home);
        //插入家和用户关系表数据
        HomeUser homeUser = HomeUser.builder()
                .homeId(home.getId())
                .userId(userAccount.getId())
                .memberUserId(userAccount.getId())
                .memberMobile(userAccount.getMobile())
                .isDefaultHome(home.getIsDefaultHome())
                .isMain(true)
                .type(AccountTypeEnum.MASTER.getCode())
                .build();

        //默认绑定4个场景
        List<SceneTemplate> list = sceneTemplateService.list();
        for (int i = 0; i < list.size(); i++) {
            //保存场景数据
            Scene scene = Scene.builder()
                    .userId(userAccount.getId())
                    .homeId(home.getId())
                    .sceneIcon(list.get(i).getBackgroundUrl())
                    .sceneName(list.get(i).getName())
                    .isDefault(1)
                    .command(list.get(i).getName())
                    .createTime(LocalDateTime.now().withSecond(i))
                    .build();
            sceneService.save(scene);
        }

        homeUserService.save(homeUser);

        //插入默认房间数据
        final List<HomeRoom> homeRooms = bizHomeService.buildDefaultRoom(home.getId(), userAccount.getId());
        homeRoomService.saveBatch(homeRooms);

        //房间实体上传
        bizUploadEntityService.uploadEntityUserLevel(userAccount.getId(), DynamicEntitiesNameEnum.RoomName);
    }
}
