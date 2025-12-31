package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 音乐产品管理
 */
@RestController
@RequestMapping("/api/auth/music/product")
public class MusicProductController {
    @Resource
    IMusicProductService musicProductService;

    /**
     * 查找音乐畅听卡
     *
     * @return
     */
    @RequestMapping("info")
    public CommonResultVo<MusicProduct> info() {
        return CommonResultVo.SUCCESS(musicProductService.list().get(0));
    }

}
