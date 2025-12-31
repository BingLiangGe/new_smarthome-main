package com.lj.iot.api.app;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.mapper.UserDeviceMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class SceneCardTest {

    @Resource
    private UserDeviceMapper userDeviceMapper;

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;

    @Test
    public void test() {
        List<String> list = userDeviceMapper.findOxHotelDevice();

        for (String deviceId : list
        ) {
            List<UserDeviceMeshKey> meshKeyList = userDeviceMeshKeyService.list(new QueryWrapper<>(UserDeviceMeshKey.builder().deviceId(deviceId).build()));
            System.out.println(deviceId + "," + list.size() + "," + meshKeyList.size());

            for (UserDeviceMeshKey key : meshKeyList
            ) {
                userDeviceMeshKeyService.removeById(key.getId());
            }
        }
    }
}
