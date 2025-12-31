package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.db.smart.entity.CommunicateLogHis;
import com.lj.iot.biz.db.smart.service.ICommunicateLogHisService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 历史通话记录
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/api/auth/communicate_log_his")
public class CommunicateLogHisController {

    @Resource
    ICommunicateLogHisService communicateLogHisService;

    /**
     * 查看呼救消息记录
     *
     * @param pageDto
     * @return
     */
    @RequestMapping("page")
    @CustomPermissions("communicate_log_his:page")
    public CommonResultVo<IPage<CommunicateLogHis>> list(@Valid PageDto pageDto) {
        return CommonResultVo.SUCCESS(communicateLogHisService.customPage(pageDto, null, UserDto.getUser().getActualUserId()));
    }
}
