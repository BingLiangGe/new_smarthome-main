/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.api.hotel.web.open;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.base.dto.LoginSmsDto;
import com.lj.iot.biz.base.vo.ExportDeviceJsonVo;
import com.lj.iot.biz.base.vo.HotelDataVo;
import com.lj.iot.biz.db.smart.BizHotelUserAccountService;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.FaceUser;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.service.IFaceUserService;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sms.service.ISmsService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 登录相关
 */
@Slf4j
@RequestMapping("api/open")
@RestController
public class OpenUserController {

    @Resource
    private ICacheService cacheService;
    @Resource
    private IHotelUserAccountService hotelUserAccountService;
    @Resource(name = "SmsServiceImpl")
    private ISmsService smsService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private BizHotelUserAccountService bizHotelUserAccountService;

    @Autowired
    private IFaceUserService faceUserService;

    /**
     * 生成虚假手机号
     *
     * @param response
     */
    @RequestMapping("/exportFaceMobile")
    public void exportFaceUser(HttpServletResponse response) throws Exception {

        List<FaceUser> faceUserList = Lists.newArrayList();

        for (int i = 0; i < 5; i++) {
            String mobile = "110" + createCode(8);
            log.info(mobile);

            FaceUser faceUser = FaceUser.builder()
                    .createTime(LocalDateTime.now())
                    .codes(createCode(6))
                    .faceMobile(mobile).build();

            faceUserList.add(faceUser);
        }
        faceUserService.saveBatch(faceUserList);

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(DateUtil.now() + "_手机号5个", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), FaceUser.class)
                .sheet("虚拟手机号")
                .doWrite(() -> {
                    return faceUserList;
                });
    }

    public static String createCode(int n) {
        Random r = new Random();
        String code = "";
        for (int i = 0; i < n; i++) {
            // 数字
            int ch2 = r.nextInt(10);
            code += ch2;
        }
        return code;
    }

    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    @PostMapping("login")
    public CommonResultVo<LoginVo<HotelUserAccount>> login(@RequestBody @Valid LoginDto loginDto) {

        check(loginDto);
        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .mobile(loginDto.getAccount())
                .build()));
        ValidUtils.isNullThrow(user, "数据不存在");

        //查询默认酒店
        HotelDataVo hotelDataVo = hotelUserService.defaultHotel(user.getId());
        ValidUtils.isNullThrow(hotelDataVo, "还没有酒店。账号异常");

        //获取权限
        List<String> perms = hotelUserService.permissions(hotelDataVo.getIsMain(), hotelDataVo.getHotelId(), hotelDataVo.getMemberUserId());

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .perms(perms)
                .actualUserId(user.getActualUserId())
                .isMain(hotelDataVo.getIsMain())
                .hotelId(hotelDataVo.getHotelId())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.<HotelUserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .params(hotelDataVo.getHotelId())
                .token(token).build());
    }

    /**
     * 注册
     *
     * @param loginDto
     * @return
     */
    @PostMapping("register")
    public CommonResultVo<LoginVo<HotelUserAccount>> register(@RequestBody @Valid LoginDto loginDto) {

        check(loginDto);

        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .mobile(loginDto.getAccount())
                .build()));
        ValidUtils.noNullThrow(user, "账号已存在");

        user = bizHotelUserAccountService.register(loginDto);

        //查询默认酒店
        HotelDataVo hotelDataVo = hotelUserService.defaultHotel(user.getId());
        ValidUtils.isNullThrow(hotelDataVo, "还没有酒店。账号异常");

        //获取权限
        List<String> perms = hotelUserService.permissions(hotelDataVo.getIsMain(), hotelDataVo.getHotelId(), hotelDataVo.getMemberUserId());

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .perms(perms)
                .actualUserId(user.getActualUserId())
                .isMain(hotelDataVo.getIsMain())
                .hotelId(hotelDataVo.getHotelId())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.<HotelUserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .token(token).build());
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

    @PostMapping("smsQy")
    public CommonResultVo<String> smsQy(@RequestBody @Valid LoginSmsDto loginDto) {
        sendSmsQy(loginDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 登录获取短信
     *
     * @param paramDto
     */
    private void sendSms(LoginSmsDto paramDto) {

        FaceUser faceUser = faceUserService.getOne(new QueryWrapper<>(FaceUser.builder()
                .faceMobile(paramDto.getAccount()).build()));

        String key = "hotel" + RedisConstant.code_check + paramDto.getAccount();
        String code = null;

        if (faceUser != null)
            code = faceUser.getCodes();
        else
            code = smsService.sendVerificationCode(paramDto.getAccount());

        cacheService.addSeconds(key, code, 60 * 5 * 1000L);
    }

    private void sendSmsQy(LoginSmsDto paramDto) {

        FaceUser faceUser = faceUserService.getOne(new QueryWrapper<>(FaceUser.builder()
                .faceMobile(paramDto.getAccount()).build()));

        String key = "hotel" + RedisConstant.code_check + paramDto.getAccount();
        String code = null;

        if (faceUser != null)
            code = faceUser.getCodes();
        else
            code = smsService.sendVerificationCodeQy(paramDto.getAccount());

        cacheService.addSeconds(key, code, 60 * 5 * 1000L);
    }

    private void check(LoginDto paramDto) {
        String key = "hotel" + RedisConstant.code_check + paramDto.getAccount();
        String redisCode = cacheService.get(key);
        ValidUtils.isFalseThrow(paramDto.getCode().equals(redisCode), "验证码不正确，请查证后再试");
        cacheService.del(key);
    }
}
