package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.DeviceIdPageDto;
import com.lj.iot.biz.db.smart.entity.SpeechRecord;
import com.lj.iot.biz.db.smart.service.ISpeechRecordService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 识别记录
 */
@RestController
@RequestMapping("api/auth/speech_record")
public class SpeechRecordController {
    @Autowired
    private ISpeechRecordService speechRecordService;

    /**
     * 记录
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.ALL)
    @RequestMapping("page")
    public CommonResultVo<IPage<SpeechRecord>> page(@Valid DeviceIdPageDto pageDto) {

        IPage<SpeechRecord> page = speechRecordService.page(PageUtil.page(pageDto),
                new QueryWrapper<>(SpeechRecord
                        .builder()
                        .deviceId(pageDto.getDeviceId())
                        .build())
                        .orderByDesc("create_time"));
        return CommonResultVo.SUCCESS(page);
    }
}
