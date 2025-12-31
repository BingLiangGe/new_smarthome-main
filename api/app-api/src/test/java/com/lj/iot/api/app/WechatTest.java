package com.lj.iot.api.app;


import cn.hutool.core.date.DateUtil;
import com.lj.iot.common.pay.wx.WeChatPayProperties;
import com.lj.iot.common.pay.wx.WeChatPayV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class WechatTest {


    @Test
    public void payTest() {
        String resp = new WeChatPayV3(new WeChatPayProperties()).unifiedOrder("12345656", new BigDecimal(0.01), "test", "test", "http://www.baidu.com", DateUtil.now(), "192.168.1.1");
        System.out.println(resp);
    }
}
