package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.dto.MusicChangeDto;
import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 音乐产品管理
 */
@RestController
@RequestMapping("/api/auth/music/product")
public class MusicProductController {
    @Resource
    IMusicProductService musicProductService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    /**
     * 查找音乐畅听卡
     *
     * @return
     */
    @RequestMapping("info")
    public CommonResultVo<MusicProduct> info() {
        return CommonResultVo.SUCCESS(musicProductService.list().get(0));
    }


    /**
     * 音乐切换
     *
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.ALL)
    @PostMapping("change")
    public CommonResultVo change(@RequestBody MusicChangeDto dto) {
        bizUserDeviceService.change(dto.getType(),dto.getDeviceId(),dto.getMusicId(),dto.getVolume());
        return CommonResultVo.SUCCESS();
    }

}
