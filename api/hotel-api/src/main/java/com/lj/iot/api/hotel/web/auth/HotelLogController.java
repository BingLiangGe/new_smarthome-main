package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.db.smart.entity.HotelLog;
import com.lj.iot.biz.db.smart.service.IHotelLogService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 酒店日志
 *
 * @author xm
 * @since 2022-10-10
 */
@RestController
@RequestMapping("api/auth/sys/log")
public class HotelLogController {

    @Autowired
    private IHotelLogService hotelLogService;

    /**
     * 列表
     */
    @GetMapping("/page")
    @CustomPermissions("log:page")
    public CommonResultVo<IPage<HotelLog>> list(PageDto pageDto) {

        IPage<HotelLog> page = hotelLogService.page(PageUtil.page(pageDto),
                new QueryWrapper<>(HotelLog.builder()
                        .hotelId(UserDto.getUser().getHotelId())
                        .build())
                        .like("username", pageDto.getSearch())
                        .like("operation", pageDto.getSearch()));

        return CommonResultVo.SUCCESS(page);
    }
}
