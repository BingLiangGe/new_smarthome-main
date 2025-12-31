package com.lj.iot.api.job;

import com.lj.iot.biz.base.dto.SceneJobParamDto;
import com.lj.iot.common.redis.service.ICacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class JobApplicationTests {

    @Autowired
    private ICacheService cacheService;
    @Test
    void contextLoads() {
        String aa="wertya,";
        System.out.println(aa.substring(0,aa.length() - 1));
        //cacheService.add("c","d");
        SceneJobParamDto sceneJobParamDto= SceneJobParamDto.builder()
                .id(111L)
                .scheduleId(999L)
                .cron("10 * * * * ?")
                .build();
        cacheService.convertAndSend("TOPIC_CHANNEL_JOB_SCENE_SAVE",sceneJobParamDto);
    }

}
