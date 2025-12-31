package com.lj.iot.biz.service.aiui;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.ISceneService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.service.ISkillPostProcessor;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.List;

@Slf4j
@Component("skillPostFoure")
public class SkillPostFoureImpl implements ISkillPostProcessor {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISceneService sceneService;

    @Resource
    BizUploadEntityService bizUploadEntityService;


    @Override
    public void handle(IntentDto intentDto) {
        UserDevice userDevice = userDeviceService.getById(intentDto.getMasterDeviceId());
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("command", intentDto.getText());
        queryWrapper.eq("user_id", intentDto.getUserId());

        if (sceneService.list(queryWrapper).isEmpty()) {
            return;
        }

        bizUploadEntityService.uploadEntityUserLevel(intentDto.getUserId(), DynamicEntitiesNameEnum.SceneCorpus);
    }
}
