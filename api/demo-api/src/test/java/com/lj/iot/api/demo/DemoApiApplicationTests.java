package com.lj.iot.api.demo;

import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import com.lj.iot.common.redis.service.ICacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApiApplicationTests {


      /*  @Autowired
        private ISmsService smsService;*/
      /*  @Autowired
        private ICacheService cacheService;*/
//    @Autowired
//    private A a;

    @Test
    void testDs() {
        /*JPUSH.async(JPushDto.builder()
                .alias(new String[]{"17336014109"})
                .alert(Alert.builder()
                        .msgType("sos")
                        .title("求助")
                        .body("用户通过设备冰箱，向您发出求助信号")
                        .build())
                .build(),  new FutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                System.out.println("============");
            }
        });

        while (true){

        }*/
    }

    @Test
    void sms() {
        //System.out.println(qService.getById(1L));
        // System.out.println(smsService.sendVerificationCode("13145231417", "345674"));
    }

    @Test
    void redis() {
        cacheService.add("test", "haha");
    }

    @Test
    void redisPush() {
        cacheService.convertAndSend("topic1", "ss");
    }

    @Autowired
    private IUploadEntityService uploadEntityService;

    @Test
    void uploadEntity() {
        List<UploadEntityItemDto> entityList = new ArrayList<>();
        entityList.add(UploadEntityItemDto.builder()
                .name("华为书包")
                .alias("华为钱包")
                .build());
        entityList.add(UploadEntityItemDto.builder()
                .name("吃饭去")
                .alias("吃放吧")
                .build());
        uploadEntityService.uploadAppLevel(UploadEntityDto.builder()
                .userId("20220810115703609716843778949121")
                .entityList(entityList)
                .build());
        //System.out.println(qService.getById(1L));
        // System.out.println(smsService.sendVerificationCode("13145231417", "345674"));
    }

    @Test
    void checkUploadEntity() {

        uploadEntityService.check("psn08652694@dx0001164d22cfa11101");
        //System.out.println(qService.getById(1L));
        // System.out.println(smsService.sendVerificationCode("13145231417", "345674"));
    }


    @Autowired
    private ICacheService cacheService;

   /* @Test
    void jobSave() {
        SceneJobParamDto sceneJobParamDto = SceneJobParamDto.builder()
                .id(111L)
                .scheduleId(999L)
                .daysOfWeek(Arrays.asList(1, 2, 3))
                .minus(4)
                .hour(3)
                .day(2)
                .month(1)
                .year(2022)
                .build();
        cacheService.convertAndSend("TOPIC_CHANNEL_JOB_SCENE_SAVE", sceneJobParamDto);

    }*/


    @Test
    void jobSave2() {

        cacheService.add("a", "b");

    }
}
