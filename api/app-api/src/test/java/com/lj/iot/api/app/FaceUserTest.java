package com.lj.iot.api.app;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IFaceUserService;
import com.lj.iot.biz.db.smart.service.IHomeService;
import com.lj.iot.biz.db.smart.service.IHomeUserService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

@Slf4j
@SpringBootTest
public class FaceUserTest {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IFaceUserService faceUserService;

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IHomeUserService homeUserService;

    @Test
    public void createFaceUser() {

       /* List<UserAccount> list = new ArrayList<>();

        List<FaceUser> faceUserList = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            String mobile = "110" + createCode(8);
            log.info(mobile);

            UserAccount userAccount = UserAccount.builder()
                    .type("1")
                    .mobile(mobile)
                    .id(IdUtils.nextId())
                    .nickname(mobile).build();

            FaceUser faceUser = FaceUser.builder()
                    .createTime(LocalDateTime.now())
                    .faceMobile(mobile).build();

            list.add(userAccount);
            faceUserList.add(faceUser);
        }

        userAccountService.saveBatch(list);
        faceUserService.saveBatch(faceUserList);*/

        List<String> faceList = faceUserService.selectAllMobile();

        for (String mobile : faceList
        ) {
            UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder().mobile(mobile).build()));

            SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(userAccount));

        }
    }

}
