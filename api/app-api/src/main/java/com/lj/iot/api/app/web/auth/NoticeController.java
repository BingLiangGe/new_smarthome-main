package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.Notice;
import com.lj.iot.biz.db.smart.service.INoticeService;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.PageUtil;
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
    public CommonResultVo<IPage<Notice>> page(@Valid PageDto dto) {
        return CommonResultVo.SUCCESS(noticeService.page(PageUtil.page(dto), new QueryWrapper<>(
                Notice.builder()
                        .userId(UserDto.getUser().getUId())
                        .build()
        ).orderByDesc("id")));
    }

    /**
     * 处理
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("handle")
    public CommonResultVo<String> handle(@RequestBody @Valid IdDto dto) {
        bizNoticeService.handle(dto, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }
}
