package com.lj.iot.api.app.web.auth;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.base.dto.AppleBindDTO;
import com.lj.iot.biz.base.dto.UserAccountEditDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.SystemMessages;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.ISystemMessagesService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.impl.AppleServiceImpl;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.minio.service.MinioService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.FtpJSch;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 用户操作相关接口
 */
@RestController
@RequestMapping("api/auth/user")
public class UserController {


    @Value("${minio.bucket-name:iot}")
    private String bucketName;

    @Resource
    private IUserAccountService userAccountService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Resource
    private MinioService minioService;

    @Autowired
    private AppleServiceImpl appleService;

    @Autowired
    private ISystemMessagesService messagesService;

    /**
     * 获取系统升级信息
     *
     * @return
     */
    @RequestMapping("/getSystemUpdateMsg")
    public CommonResultVo<SystemMessages> getSystemUpdateMsg() {
        UserAccount user = userAccountService.getById(UserDto.getUser().getUId());

        if (user == null) {
            return CommonResultVo.FAILURE_MSG("未登录");
        }

        List<SystemMessages> list = messagesService.list(new QueryWrapper<>(SystemMessages.builder().userId(user.getId())
                .type(5)
                .readType(0).build()).orderByDesc("create_time"));

        if (!list.isEmpty()) {
            return CommonResultVo.SUCCESS(list.get(0));
        }
        return CommonResultVo.SUCCESS();
    }


    /**
     * 绑定苹果账号
     *
     * @param appleBindDTO
     * @return
     */
    @RequestMapping("/bindApple")
    public CommonResultVo<String> bindApple(@RequestBody AppleBindDTO appleBindDTO) {
        UserAccount user = userAccountService.getById(UserDto.getUser().getUId());

        if (user == null) {
            return CommonResultVo.FAILURE_MSG("未登录");
        }

        String appId = appleService.appleLogin(appleBindDTO.getIdentityToken());

        if (appId == null) {
            return CommonResultVo.FAILURE_MSG("苹果登录授权失败");
        }

        appleLoginHandle(appId, user.getMobile());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 微信登录
     *
     * @param appId
     * @return
     */
    public UserAccount appleLoginHandle(String appId, String mobile) {

        //先判断有没有微信
        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .appleId(appId)
                .build()));
        //协助登陆
        if (user == null) {
            UserAccount user1 = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(mobile)
                    .build()));
            if (user1 == null) {
                String id = IdUtils.nextId();
                user = UserAccount.builder()
                        .id(id)
                        .actualUserId(id)
                        .appleId(appId)
                        .mobile(mobile)
                        .nickname(mobile)
                        .type(AccountTypeEnum.MASTER.getCode())
                        .build();
                userAccountService.save(user);
                //事件监听类
                SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(user));
                return user;
            } else {
                user1.setAppleId(appId);
                userAccountService.updateById(user1);
                return user1;
            }
        }
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }

    /**
     * 解除apple账号绑定
     *
     * @return
     */
    @PostMapping("/unbindingApple")
    public CommonResultVo<LoginVo<UserAccount>> unbindingApple() {
        UserAccount user = userAccountService.getById(UserDto.getUser().getUId());

        user.setType(AccountTypeEnum.MASTER.getCode());
        user.setAppleId("");

        userAccountService.updateById(user);

        return CommonResultVo.SUCCESS();
    }


    /**
     * 个人详情
     *
     * @return
     */
    @RequestMapping("/info")
    public CommonResultVo<UserAccount> info() {
        return CommonResultVo.SUCCESS(userAccountService.getById(UserDto.getUser().getUId()));
    }


    /**
     * 上传头像
     *
     * @return
     */
    @PostMapping("/upload_portrait")
    public CommonResultVo<String> uploadPortrait(@RequestParam("file") MultipartFile file) {
        //JSONObject json = minioService.uploadFile(file, "", bucketName);

        String filePath = "http://47.100.238.205:8888/appPortrait/";
        try {
            String fileName = FtpJSch.uploadImg(file.getInputStream(), file.getOriginalFilename());
            filePath += fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userAccountService.updateById(UserAccount.builder().id(UserDto.getUser().getActualUserId())
                .avatarUrl(filePath).build());
        return CommonResultVo.SUCCESS(filePath);
    }


    /**
     * 修改用户信息
     *
     * @param dto
     * @return
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("/edit")
    public CommonResultVo<UserAccount> edit(@RequestBody @Valid UserAccountEditDto dto) {
        return CommonResultVo.SUCCESS(userAccountService.edit(dto, UserDto.getUser().getUId()));
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("logout")
    public CommonResultVo<String> logout() {
        LoginUtils.logout(UserDto.getUser());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 注销
     *
     * @return
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("cancellation")
    public CommonResultVo<String> cancellation() {
        bizUserAccountService.cancellation(UserDto.getUser().getUId());
        LoginUtils.logout(UserDto.getUser());
        return CommonResultVo.SUCCESS();
    }
}
