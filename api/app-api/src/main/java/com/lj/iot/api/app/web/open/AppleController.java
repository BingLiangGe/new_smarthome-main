package com.lj.iot.api.app.web.open;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.base.dto.AppleLoginDTO;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.HomeUser;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IHomeUserService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.impl.AppleServiceImpl;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 苹果路由
 */
@RestController
@RequestMapping("/api/open/apple")
public class AppleController {

    @Autowired
    private AppleServiceImpl appleService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHomeUserService homeUserService;

    @Autowired
    private ICacheService cacheService;


    /**
     * 获取苹果登录用户信息
     *
     * @param appId
     * @return
     */
    @GetMapping("/getAppleLoginInfo")
    public CommonResultVo<LoginVo<UserAccount>> getAppLoginInfo(String appId) {

        if (appId == null) {
            return CommonResultVo.FAILURE_MSG("appId必传");
        }

        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .appleId(appId)
                .build()));

        //协助登陆
        if (user != null) {
            //登录token 缓存token
            String token = LoginUtils.login(UserDto.builder()
                    .uId(user.getId())
                    .account(user.getMobile())
                    .actualUserId(user.getActualUserId())
                    .build());
            HomeUser homeUser = homeUserService.defaultHome(user.getId());

            return CommonResultVo.SUCCESS(LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token(token)
                    .params(homeUser.getHomeId())
                    .build());
        }
        return CommonResultVo.SUCCESS();
    }



    /**
     * APP登录
     *
     * @param dto
     * @return
     */
    @RequestMapping("/appleLogin")
    public CommonResultVo<LoginVo<UserAccount>> appleLogin(@RequestBody AppleLoginDTO dto) {
        String appId = appleService.appleLogin(dto.getIdentityToken());

        if (appId == null) {
            return CommonResultVo.FAILURE_MSG("苹果登录授权失败");
        }

        String key = "app" + RedisConstant.code_check +dto.getMobile();
        String redisCode = cacheService.get(key);

        //ValidUtils.isFalseThrow(dto.getCode().equals(redisCode), "验证码不正确，请查证后再试");

        UserAccount user = appleLoginHandle(appId, dto.getMobile());

        HomeUser homeUser = homeUserService.defaultHome(user.getId());
        ValidUtils.isNullThrow(homeUser, "没有家庭，账号异常");

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.<UserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .token(token)
                .params(homeUser.getHomeId())
                .build());
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
}
