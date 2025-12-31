package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.NoticePageDto;
import com.lj.iot.biz.base.vo.NoticeVo;
import com.lj.iot.biz.db.smart.entity.Notice;
import com.lj.iot.biz.db.smart.service.INoticeService;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 消息
 */
@RestController
@RequestMapping("api/auth/notice")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private BizNoticeService bizNoticeService;

    /**
     * 分页
     */
    @RequestMapping("page")
    @CustomPermissions("notice:page")
    public CommonResultVo<NoticeVo<Notice>> page(@Valid NoticePageDto dto) {

        System.out.println("homeId:"+dto.getHomeId());
        IPage<Notice> page = noticeService.customPage(dto, UserDto.getUser().getHotelId()
                , UserDto.getUser().getActualUserId());

        long unHandle = noticeService.unHandle(dto, UserDto.getUser().getHotelId()
                , UserDto.getUser().getActualUserId());

        return CommonResultVo.SUCCESS(NoticeVo.<Notice>builder()
                .unHandle(unHandle)
                .page(page)
                .build());
    }

    /**
     * 处理
     */
    @PostMapping("handle")
    @CustomPermissions("notice:handle")
    public CommonResultVo<String> handle(@RequestBody @Valid IdDto dto) {
        bizNoticeService.handle(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }
}
