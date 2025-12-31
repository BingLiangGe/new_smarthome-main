package com.lj.iot.api.app.web.open;

import com.lj.iot.biz.db.smart.entity.Banner;
import com.lj.iot.biz.db.smart.service.IBannerService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * 轮播图接口
 * 
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/open/banner")
public class BannerController {
    @Autowired
    IBannerService bannerService;

    /**
     * 查询轮播图
     */
    @GetMapping("list")
    public CommonResultVo<List<Banner>> list() {
        return CommonResultVo.SUCCESS(bannerService.list());
    }
}
