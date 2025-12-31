package com.lj.iot.api.app;

import com.google.common.collect.Lists;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
public class EntityTest {

    @Resource
    IUploadEntityService uploadEntityService;

    @Test
    public void cleanEntity() {

        List<UploadEntityItemDto> uploadEntityItemList = Lists.newArrayList();

        uploadEntityItemList.add(UploadEntityItemDto.builder()
                .name("00000").build());


        uploadEntityService.uploadCustomLevelTrigger(UploadEntityDto.builder().entityList(uploadEntityItemList)
                .dynamicEntitiesName(DynamicEntitiesNameEnum.SceneCorpus.getCode())
                .userId("20231006103202762623128286310400").build());

    }
}
