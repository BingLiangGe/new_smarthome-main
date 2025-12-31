package com.lj.iot.api.app.web.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.lj.iot.biz.db.smart.entity.MusicMenuTop;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IMusicMenuService;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 音乐列表相关接口
 *
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@Slf4j
@RestController
@RequestMapping("api/open/muscimenu")
public class MusicMenuController {
    @Resource
    IMusicMenuService musicMenuService;

    @Resource
    IUserDeviceService userDeviceService;

    @Autowired
    private IDeviceService deviceService;

    @Resource
    MqttPushService mqttPushService;

    @Resource
    IMusicProductService musicProductService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    /**
     * 音乐列表
     * @return
     */
    @GetMapping("findMusicMenuList")
    public CommonResultVo<List<MusicMenu>> findMusicMenuList(MusicMenu musicMenu) {
        List<MusicMenu> list = musicMenuService.findUserid(musicMenu.getDeviceId());
        return CommonResultVo.SUCCESS(list);
    }




    /**
     * 音乐存储
     * @return
     */
    @PostMapping("saveMusicMenuList")
    public CommonResultVo<Boolean> saveMusicMenuList(@RequestBody @Valid List<MusicMenu> list) {
        log.info("saveMusicMenuList====={}", JSON.toJSONString(list));

        String uId = UserDto.getUser().getUId();
        //先清空数据
        musicMenuService.removeAll(list.get(0).getDeviceId());

        musicMenuService.saveBatch(list);
        JSONObject params = new JSONObject();
        String deviceId = list.get(0).getDeviceId();
        UserDevice byId = userDeviceService.getById(deviceId);
        ArrayList<MusicMenuTop> list1 = new ArrayList<MusicMenuTop>();
        for (int i = 0; i < list.size(); i++) {
            MusicMenuTop musicMenuTop = new MusicMenuTop();
            musicMenuTop.setMusicId(list.get(i).getMusicId());
            musicMenuTop.setStatus(list.get(i).getStatus());
            list1.add(musicMenuTop);
        }
        params.put("MusicMenuList",list1);
        mqttPushService.musicMenu(byId,list1);
        return CommonResultVo.SUCCESS();
    }



    /**
     * 更新
     * @return
     */
    @PostMapping("updateMusic")
    public CommonResultVo<Boolean> updateMusicMenuList(@Valid MusicMenu musicMenu) {
        log.info("updateMusic====={}", JSON.toJSONString(musicMenu));

        MusicMenu one = musicMenuService.selectByMusicMenu(musicMenu.getMusicId() ,musicMenu.getDeviceId());
        log.info("one====={}", one);
        //设置全部为 非默认 和 停止
        musicMenuService.update(MusicMenu.builder()
                .isDefaultMusic(false)
                .status(1)
                .build(),
                new QueryWrapper<>(MusicMenu.builder()
                        .deviceId(musicMenu.getDeviceId())
                        .build()));


        UserDevice byId = userDeviceService.getById(musicMenu.getDeviceId());
        switch (musicMenu.getStatus()){
            case 0: //切换歌
                bizUserDeviceService.change(String.valueOf(musicMenu.getStatus()),musicMenu.getDeviceId(),musicMenu.getMusicId(),"");
                break;
            case 1: //暂停歌
                mqttPushService.musicChange(byId,String.valueOf(musicMenu.getStatus()),musicMenu.getMusicId(),"");
                //更新当前数据
                break;
            case 2://开始歌
                mqttPushService.musicChange(byId,String.valueOf(musicMenu.getStatus()),musicMenu.getMusicId(),"");
                break;
            case 3:
                mqttPushService.musicChange(byId,String.valueOf(musicMenu.getStatus()),musicMenu.getMusicId(),musicMenu.getVolume());
                break;
            case 4:
                mqttPushService.musicChange(byId,String.valueOf(musicMenu.getStatus()),musicMenu.getMusicId(),"");
                break;
            default :
        }

        if(musicMenu.getStatus()==0){
            one.setDefaultMusic(true);
            musicMenuService.updateById(one);
        }
        if(musicMenu.getStatus()==2|musicMenu.getStatus()==3){
            one.setStatus(musicMenu.getStatus());
            one.setDefaultMusic(true);
            musicMenuService.updateById(one);
        }

        return CommonResultVo.SUCCESS();
    }
}
