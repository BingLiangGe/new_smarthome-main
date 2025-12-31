package com.lj.iot.api.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.lj.iot.biz.db.smart.service.IMusicOrderService;
import com.lj.iot.biz.service.BizMusicOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class MusicTest {

    @Autowired
    private BizMusicOrderService bizMusicOrderService;

    @Autowired
    private IMusicOrderService musicOrderService;

    @Test
    public void test() {
        bizMusicOrderService.active("162702ec2e02", "20231006103202762623128286310400");
    }
}
