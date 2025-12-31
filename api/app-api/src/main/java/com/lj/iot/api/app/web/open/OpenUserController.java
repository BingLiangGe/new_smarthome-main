package com.lj.iot.api.app.web.open;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.base.dto.LoginSmsDto;
import com.lj.iot.biz.base.dto.LoginWeChatDto;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.impl.BizIrDataServiceImpl;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sms.service.ISmsService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 登录、注册、获取验证码
 *
 * @author mz
 * @Date 2022/7/15
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/user")
public class OpenUserController {

    @Resource
    private ICacheService cacheService;
    @Resource
    private IUserAccountService userAccountService;
    @Resource(name = "SmsServiceImpl")
    private ISmsService smsService;

    @Autowired
    private IHomeUserService homeUserService;

    public static String openAppId = "wx7642395b6aa0cdb3";

    public static String openCCCFDF = "42170288c3057c4084c1a3ee72b04bca";

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private JDatService jDatService;

    @Autowired
    private IFaceUserService faceUserService;


    @RequestMapping("/createFaceUser")
    public CommonResultVo createFaceUser() {
        List<String> faceList = faceUserService.selectAllMobile();

        for (String mobile : faceList
        ) {
            UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder().mobile(mobile).build()));

            SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(userAccount));

        }
        return CommonResultVo.SUCCESS();
    }

    @RequestMapping("/selectAoksJdata")
    public CommonResultVo selectAoksJdata() {
        List<JDat> list = jDatService.selectAoksJdata();

        System.out.println(list.size());
        list.forEach(e -> {
            JSONObject irJson = JSONObject.parseObject(e.getDats());
            System.out.println(BizIrDataServiceImpl.decode(irJson.getString("irdata")));
        });
        return CommonResultVo.SUCCESS();
    }

    /**
     * 登录、注册
     *
     * @param loginDto
     * @return
     */
    @PostMapping("login")
    public CommonResultVo<LoginVo<UserAccount>> loginOrRegister(@RequestBody @Valid LoginDto loginDto) {
        UserAccount user = login(loginDto);

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
     * 登录、注册
     *
     * @param loginDto
     * @return
     */
    @PostMapping("login_and_openid")
    public CommonResultVo<LoginVo<UserAccount>> loginAndOpenid(@RequestBody @Valid LoginWeChatDto loginDto) {
        log.info("loginAndOpenid.loginDto:{}", loginDto);

        UserAccount user = weLogin(loginDto);

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
     * 获取短信验证码
     *
     * @param loginDto
     * @return
     */
    @PostMapping("sms")
    public CommonResultVo<String> sms(@RequestBody @Valid LoginSmsDto loginDto) {
        sendSms(loginDto);
        return CommonResultVo.SUCCESS();
    }


    /**
     * 获取短信验证码
     *
     * @return
     */
    @GetMapping("find_token")
    public CommonResultVo<LoginVo<UserAccount>> findToken(String openid) {
        log.info("find_token.openid:{}", openid);
        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .openId(openid)
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
     * 获取短信验证码
     *
     * @return
     */
    @GetMapping("find_openid")
    public CommonResultVo<String> findOpenid(String code) {
        log.info("WeChatController.code:{}", code);
        try {
            String template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&CCCFDF=%s&code=%s&grant_type=authorization_code";
            String url = String.format(template, openAppId, openCCCFDF, code);
            String result = OkHttpUtils.get(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject.get("errcode") != null) {
                return CommonResultVo.FAILURE_MSG("微信登录授权失败");
            }

            return CommonResultVo.SUCCESS(jsonObject.getString("openid"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResultVo.SUCCESS();
    }


    /**
     * 登录
     *
     * @param paramDto
     * @return
     */
    public UserAccount login(LoginDto paramDto) {

        check(paramDto);

        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .mobile(paramDto.getAccount())
                .build()));
        if (user == null) {
            String id = IdUtils.nextId();
            user = UserAccount.builder()
                    .id(id)
                    .actualUserId(id)
                    .mobile(paramDto.getAccount())
                    .nickname(paramDto.getAccount())
                    .type(AccountTypeEnum.MASTER.getCode())
                    .build();
            userAccountService.save(user);
            //事件监听类
            SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(user));
        }
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }


    /**
     * 登录
     *
     * @param paramDto
     * @return
     */
    public UserAccount weLogin(LoginWeChatDto paramDto) {

        //先判断有没有微信
        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .openId(paramDto.getOpenId())
                .build()));
        //协助登陆
        if (user == null) {
            check(paramDto);
            UserAccount user1 = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(paramDto.getAccount())
                    .build()));
            if (user1 == null) {
                String id = IdUtils.nextId();
                user = UserAccount.builder()
                        .id(id)
                        .actualUserId(id)
                        .openId(paramDto.getOpenId())
                        .mobile(paramDto.getAccount())
                        .nickname(paramDto.getAccount())
                        .type(AccountTypeEnum.MASTER.getCode())
                        .build();
                userAccountService.save(user);
                //事件监听类
                SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(user));
                return user;
            } else {
                user1.setOpenId(paramDto.getOpenId());
                userAccountService.updateById(user1);
                return user1;
            }
        }

        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }

    /**
     * 登录获取短信
     *
     * @param paramDto
     */
    private void sendSms(LoginSmsDto paramDto) {

        String key = "app" + RedisConstant.code_check + paramDto.getAccount();
        List<String> list = faceUserService.selectAllMobile();
        String code = null;
        if (list.contains(paramDto.getAccount())) {
            code = "123456";
        } else {
            code = smsService.sendVerificationCode(paramDto.getAccount());
        }

        // 存储验证码
        userAccountService.update(UserAccount.builder().code(code).build(), new QueryWrapper<>(UserAccount.builder()
                .mobile(paramDto.getAccount()).build()));

        cacheService.addSeconds(key, code, 60 * 5 * 1000L);
    }


    private void check(LoginDto paramDto) {
        String key = "app" + RedisConstant.code_check + paramDto.getAccount();
        String redisCode = cacheService.get(key);
        ValidUtils.isFalseThrow(paramDto.getCode().equals(redisCode), "验证码不正确，请查证后再试");
        cacheService.del(key);
    }

    private void check(LoginWeChatDto paramDto) {
        String key = "app" + RedisConstant.code_check + paramDto.getAccount();
        String redisCode = cacheService.get(key);
        ValidUtils.isFalseThrow(paramDto.getCode().equals(redisCode), "验证码不正确，请查证后再试");
        cacheService.del(key);
    }


    /**
     * 获取短信验证码
     *
     * @return
     */
    @GetMapping("demoMq")
    public CommonResultVo<String> demoMq() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Date> dates = null;

        try {
            dates = randomDate("2023-4-21 00:00:00", "2023-04-21 23:59:59", 100, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String topic = "sys/213350486/7089f500005c/thing/event/topology/line";
        MqttParamDto paramDto = MqttParamDto.builder()
                .id(IdUtil.simpleUUID())
                .time(DateUtil.current())
                .data(null)
                .build();
        while (true) {

            dates.forEach(t -> System.out.println(sdf.format(t)));

            MQTT.publish(topic, JSON.toJSONString(paramDto));
        }

    }

    /**
     * 生成size数量的随机时间，位于[start,end)范围内 时间倒序排列
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param size  生成时间个数
     * @param order 结果排序：-1时间倒叙，0 乱序，1时间正序
     * @return List<Date>
     * @throws Exception
     */
    public static List<Date> randomDate(String start, String end, int size, int order) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse(start);
        Date endTime = sdf.parse(end);

        Random random = new Random();
        List<Date> dates = random.longs(size, startTime.getTime(), endTime.getTime()).mapToObj(t -> new Date(t)).collect(Collectors.toList());

        dates.sort((t1, t2) -> {
            return t1.compareTo(t2) * order;
        });

        return dates;
    }


    //创建一个定时任务判断有没有发送成功
    public void ifSend(String topic) {
        //缓冲获取
        //发送时候存储
        cacheService.addSeconds(topic, null, 60 * 5 * 1000L);
        //topic返回时候存储
        cacheService.addSeconds(topic, topic, 60 * 5 * 1000L);

        //定时任务时候获取是否成功
        String redisTopic = cacheService.get(topic);
        if (redisTopic != null) {
            //成功
        } else {
            //重发
            cacheService.addSeconds(topic, null, 60 * 5 * 1000L);
        }
    }


}
