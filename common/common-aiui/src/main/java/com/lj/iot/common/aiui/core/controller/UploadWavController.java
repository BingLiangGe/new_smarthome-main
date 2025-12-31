package com.lj.iot.common.aiui.core.controller;

import com.lj.iot.common.aiui.core.dto.AIUIWsDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.ISkillWsAckProcessor;
import com.lj.iot.common.aiui.core.ws.AiuiWsClient;
import com.lj.iot.common.base.vo.CommonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * 上传语音文件
 *
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/aiui")
public class UploadWavController {

    @Autowired
    private AiuiProperties properties;
    @Autowired
    private ISkillWsAckProcessor skillWsAckProcessor;


    /**
     * 上传语音文件
     *
     * @param file
     * @param aiuiWsDto
     * @return
     */
    @RequestMapping("upload")
    public CommonResultVo upload(@Valid @RequestParam("file") MultipartFile file, AIUIWsDto aiuiWsDto) {
        try {
            AiuiWsClient.uploadFile(aiuiWsDto, file.getBytes(), skillWsAckProcessor);
        } catch (Exception e) {
            return CommonResultVo.FAILURE_MSG("语音有问题");
        }
        return CommonResultVo.SUCCESS("好的");
    }
}
