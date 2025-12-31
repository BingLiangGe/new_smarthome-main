package com.lj.iot.api.system.web.open;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.vo.HotelDataVo;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static javax.management.Query.TIMES;

@Slf4j
@RequestMapping("/api/open/user")
@RestController
public class LoginController {

    private static final String APP_ID = "888888";
    private static final String APP_KEY = "16eb1fe65b674059a2a97e1a78c31b3c";

    @Resource
    private IHotelUserAccountService hotelUserAccountService;


    @Autowired
    private IHotelUserService hotelUserService;


    /**
     * 登录_获取token
     *
     * @return
     */
    @RequestMapping("/login")
    public CommonResultVo<String> login(@RequestBody Map<String, Object> params) {

        ValidUtils.isNullThrow(params, "参数必传");
        ValidUtils.isNullThrow(params.get("appId"), "appId 参数必传");
        ValidUtils.isNullThrow(params.get("sign"), "sign 参数必传");
        ValidUtils.isNullThrow(params.get("time"), "time 参数必传");
        ValidUtils.isNullThrow(params.get("mobile"), "mobile 参数必传");

        if (!validation(params)) {
            return CommonResultVo.FAILURE_MSG("sign error!");
        }

        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .mobile(String.valueOf(params.get("mobile")))
                .build()));

        ValidUtils.isNullThrow(user, "数据不存在");

        //查询默认酒店
        HotelDataVo hotelDataVo = hotelUserService.defaultHotel(user.getId());
        ValidUtils.isNullThrow(hotelDataVo, "还没有酒店。账号异常");

        //获取权限
        List<String> perms = hotelUserService.permissions(hotelDataVo.getIsMain(), hotelDataVo.getHotelId(), hotelDataVo.getMemberUserId());


        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.FOURFRIENDS.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .perms(perms)
                .actualUserId(user.getActualUserId())
                .isMain(hotelDataVo.getIsMain())
                .hotelId(hotelDataVo.getHotelId())
                .build());

        return CommonResultVo.SUCCESS(token);
    }

    @RequestMapping("/getSign")
    public CommonResultVo<String> getSign(String mobile) {

        JSONObject data = new JSONObject();
        data.put("mobile", mobile);
        data.put("appId", "888888");
        data.put("time", System.currentTimeMillis());


        log.info("time={}", data.get("time"));

        String sign = getSign(data);

        return CommonResultVo.SUCCESS(sign);
    }


    private boolean validation(Map<String, Object> params) {
        String sign = String.valueOf(params.get("sign"));

        params.remove("sign");

        String sign1 = getSign(params);
        params.remove("sign");

        log.info("sign1={},sign={}", sign1, sign);
        // 校验签名
        if (!StringUtils.equals(sign1, sign)) {// APPID查询的密钥进行签名 和 用户签名进行比对
            return false;
        }
        // 校验签名是否失效
        long thisTime = System.currentTimeMillis() - Long.valueOf((String) params.get("time"));
        log.info("thisTIme={}", thisTime);
        if (thisTime > 120000) {// 比对时间是否失效
            return false;
        }
        return true;
    }


    /**
     * 计算签名
     *
     * @return
     */
    public static String getSign(Map<String, Object> params) {
        // 参数进行字典排序
        String sortStr = getFormatParams(params);
        // 将密钥key拼接在字典排序后的参数字符串中,得到待签名字符串。
        sortStr += "key=" + APP_KEY;
        // 使用md5算法加密待加密字符串并转为大写即为sign
        String sign = SecureUtil.md5(sortStr).toUpperCase();
        return sign;
    }

    /**
     * 参数字典排序
     *
     * @param params
     * @return
     */
    public static String getFormatParams(Map<String, Object> params) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(params.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> arg0, Map.Entry<String, Object> arg1) {
                return (arg0.getKey()).compareTo(arg1.getKey());
            }
        });
        String ret = "";
        for (Map.Entry<String, Object> entry : infoIds) {
            ret += entry.getKey();
            ret += "=";
            ret += entry.getValue();
            ret += "&";
        }
        return ret;
    }
}
