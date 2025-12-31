package com.lj.iot.api.app.web.open;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.lj.iot.api.app.web.event.RegisterEvent;
import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.base.dto.WeChatLoginDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.BizMusicOrderService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.pay.wx.WeChatPayV3;
import com.lj.iot.common.pay.wx.WxPayNotifyReq;
import com.lj.iot.common.pay.wx.util.WeChatUtils;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 微信相关接口
 */
@Slf4j
@RestController
@RequestMapping("api/open/wechat")
public class WeChatController {

    @Resource
    private WeChatPayV3 weChatPayV3;
    @Resource
    private ICacheService cacheService;

    @Autowired
    private BizMusicOrderService bizMusicOrderService;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private IUserAccountService userAccountService;

    public static String openAppId = "wx7642395b6aa0cdb3";

    public static String openCCCFDF = "42170288c3057c4084c1a3ee72b04bca";

    /**
     * 微信公众号授权登录
     *
     * @param dto 微信授权后返回的code
     * @return
     */
    @PostMapping("login")
    public CommonResultVo<LoginVo<UserAccount>> login(@RequestBody @Valid WeChatLoginDto dto) {
        log.info("WeChatController.code:{}", dto.getCode());
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(dto.getCode());
            UserAccount user = loginHandle(wxMpOAuth2AccessToken.getOpenId());

            //登录token 缓存token
            String token = LoginUtils.login(UserDto.builder()
                    .uId(user.getId())
                    .account(user.getMobile())
                    .actualUserId(user.getActualUserId())
                    .build());

            return CommonResultVo.SUCCESS(LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token(token).build());
        } catch (Exception e) {
            log.error("WeChatController.login", e);
            return CommonResultVo.FAILURE_MSG("微信登录授权失败");
        }
    }





    /**
     * 微信开放平台授权登录
     *
     * @param dto 微信授权后返回的code
     * @return
     */
    @PostMapping("login2")
    public CommonResultVo<LoginVo<UserAccount>> login2(@RequestBody @Valid WeChatLoginDto dto) {
        log.info("WeChatController.code:{}", dto.getCode());
        try {

            String template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&CCCFDF=%s&code=%s&grant_type=authorization_code";
            String url = String.format(template, openAppId, openCCCFDF, dto.getCode());
            String result = OkHttpUtils.get(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject.get("errcode") != null) {
                return CommonResultVo.FAILURE_MSG("微信登录授权失败");
            }

            UserAccount user = loginHandle(jsonObject.getString("openid"));

            //登录token 缓存token
            String token = LoginUtils.login(UserDto.builder()
                    .uId(user.getId())
                    .account(user.getMobile())
                    .actualUserId(user.getActualUserId())
                    .build());

            return CommonResultVo.SUCCESS(LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token(token).build());
        } catch (Exception e) {
            log.error("WeChatController.login", e);
            return CommonResultVo.FAILURE_MSG("微信登录授权失败");
        }
    }




    /**
     * 微信开放平台授权登录
     *
     * @param dto 微信授权后返回的code
     * @return
     */
    @PostMapping("login3")
    public CommonResultVo<LoginVo<UserAccount>> login3(@RequestBody @Valid WeChatLoginDto dto) {
        log.info("WeChatController.dto:{}", dto);
        try {
            UserAccount user = null;
            if(dto.getSmscode()!=null){
                //微信绑手机
                check(dto.getUserId(),dto.getSmscode(),dto.getMobile());
                user = loginHandle1(dto);
            }
            //手机绑微信
                if (dto.getCode()!=null){
                    String template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&CCCFDF=%s&code=%s&grant_type=authorization_code";
                    String url = String.format(template, openAppId, openCCCFDF, dto.getCode());
                    String result = OkHttpUtils.get(url);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.get("errcode") != null) {
                        return CommonResultVo.FAILURE_MSG("微信登录授权失败");
                    }
                    user = loginHandle2(jsonObject.getString("openid"),dto);
                }

                return CommonResultVo.SUCCESS();




        } catch (Exception e) {
            log.error("WeChatController.login", e);
            return CommonResultVo.FAILURE_MSG("微信登录授权失败");
        }
    }



    /**
     * 微信开放平台授权登录
     *
     * @param dto 微信授权后返回的code
     * @return
     */
    @PostMapping("bind_wechat")
    public CommonResultVo<LoginVo<UserAccount>> bindWechat(@RequestBody @Valid WeChatLoginDto dto) {
        log.info("WeChatController.dto:{}", dto);
        String result = null;
        try {
            //手机绑微信
                String template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&CCCFDF=%s&code=%s&grant_type=authorization_code";
                String url = String.format(template, openAppId, openCCCFDF, dto.getCode());
                result = OkHttpUtils.get(url);
        } catch (Exception e) {
            log.error("WeChatController.login", e);
            return CommonResultVo.FAILURE_MSG("微信登录授权失败");
        }
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.get("errcode") != null) {
                    return CommonResultVo.FAILURE_MSG("微信登录授权失败");
                }
        UserAccount user = loginHandle3(jsonObject.getString("openid"), dto);


            return CommonResultVo.SUCCESS();


    }



    /**
     * 微信开放平台授权登录
     *
     * @return
     */
    @PostMapping("remove_wechat")
    public CommonResultVo<LoginVo<UserAccount>> removeWechat() {
        //删除微信
        UserAccount user = userAccountService.getById(UserDto.getUser().getUId());
        user.setType(AccountTypeEnum.MASTER.getCode());
        user.setOpenId("");
        userAccountService.updateById(user);
        return CommonResultVo.SUCCESS();


    }
    /**
     * 公众号登录
     *
     * @param
     * @return
     */
    public UserAccount loginHandle(String openId) {

        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .openId(openId)
                .build()));
        if (user == null) {
            String id = IdUtils.nextId();
            String account = IdUtils.nextTimestamp() + "";
            user = UserAccount.builder()
                    .id(id)
                    .actualUserId(id)
                    .mobile(account)
                    .nickname(account)
                    .type(AccountTypeEnum.MASTER.getCode())
                    .openId(openId)
                    .build();
            userAccountService.save(user);
            //事件监听类
            SpringUtil.getApplicationContext().publishEvent(new RegisterEvent(user));
        }
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }


    /**
     * 公众号登录
     *
     * @param
     * @return
     */
    public UserAccount loginHandle1(WeChatLoginDto dto) {

        List<UserAccount> list = userAccountService.list(new QueryWrapper<>(UserAccount.builder().mobile(dto.getMobile()).build()));
        ValidUtils.isTrueThrow(list.size()>0, "号码已被绑定");
        UserAccount user = userAccountService.getById(dto.getUserId());
            //  首次微信登陆的用户更新一下电话
        user.setType(AccountTypeEnum.MASTER.getCode());
        user.setMobile(dto.getMobile());
        userAccountService.updateById(user);
        return user;
    }



    /**
     * 公众号登录
     *
     * @param
     * @return
     */
    public UserAccount loginHandle2(String openId,WeChatLoginDto dto) {
        UserAccount useraccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .openId(openId)
                .build()));

        UserAccount user = userAccountService.getById(dto.getUserId());
        user.setType(AccountTypeEnum.MASTER.getCode());
        user.setOpenId(openId);
        userAccountService.updateById(user);
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }




    /**
     * 公众号登录
     *
     * @param
     * @return
     */
    public UserAccount loginHandle3(String openId,WeChatLoginDto dto) {
        UserAccount useraccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .openId(openId)
                .build()));
        ValidUtils.noNullThrow(useraccount, "微信已被绑定");

        UserAccount user = userAccountService.getById(dto.getUserId());
        user.setType(AccountTypeEnum.MASTER.getCode());
        user.setOpenId(openId);
        userAccountService.updateById(user);
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(user.getType()), "账号类型有误");
        return user;
    }

    @PostMapping("music/order/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        String message = "";
        try {
            message = WeChatUtils.getWxPayNotifyRequestBody(request);
        } catch (Exception e) {
            log.error("wxpayOrder error:", e);
        }
        WxPayNotifyReq wxPayNotifyReq = JSON.parseObject(message, WxPayNotifyReq.class);
        log.info("wxPayNotify:{}  message:{}", wxPayNotifyReq, message);
        String msg = wxPayNotifyReq.getSummary();
        String code = "FAIL";
        int httpCode = 500;
        try {
            //校验签名
            String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
            String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
            String wechatpaySignature = request.getHeader("Wechatpay-Signature");
            String wechatpaySerial = request.getHeader("Wechatpay-Serial");
            String str = wechatpayTimestamp + "\n" + wechatpayNonce + "\n" + message + "\n";
            log.info("wxPayNotify str:{}", str);
            boolean isFlag = weChatPayV3.verify(wechatpaySerial, str, wechatpaySignature);
            log.info("wxPayNotify isFlag:{}", isFlag);
            //校验不通过直接返回
            if (!isFlag) {
                writeMessage(response, httpCode, code, msg);
                return;
            }

            WxPayNotifyReq.PayResource payResource = wxPayNotifyReq.getResource();
            if (wxPayNotifyReq.getEvent_type().equalsIgnoreCase("TRANSACTION.SUCCESS")
                    && wxPayNotifyReq.getResource_type().equalsIgnoreCase("encrypt-resource")
                    && payResource != null) {//支付成功

                httpCode = 200;
                code = "SUCCESS";
                msg = "支付成功";

                log.info("支付成功");
                String decrypt = weChatPayV3.decryptToString(payResource.getAssociated_data().getBytes(StandardCharsets.UTF_8)
                        , payResource.getNonce().getBytes(StandardCharsets.UTF_8), payResource.getCiphertext());
                log.info("decrypt:{}", decrypt);
                if (!StringUtils.isEmpty(decrypt)) {
                    JSONObject resultData = JSON.parseObject(decrypt);
                    //处理业务逻辑
                    if (resultData != null && resultData.size() > 0) {
                        String orderNo = resultData.getString("out_trade_no");
                        String transactionId = resultData.getString("transaction_id");
                        bizMusicOrderService.completeOrder(orderNo, transactionId);
                        log.debug("wxpayOrder orderNo:{} transactionId:{}", orderNo, transactionId);
                    }
                } else {
                    log.info("支付失败");
                }
            }

        } catch (Exception e) {
            log.error("微信支付回调报错:", e);
        }

        writeMessage(response, httpCode, code, msg);
    }

    private static void writeMessage(HttpServletResponse response, int httpCode, String code, String message) {
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            writer = response.getWriter();
            response.setStatus(httpCode);

            Map<String, String> map = Maps.newHashMap();
            map.put("code", code);
            map.put("message", message);

            ObjectMapper mapper = new ObjectMapper();
            writer.println(mapper.writeValueAsString(map));
        } catch (IOException e) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void check(String userid , String smscode,String mobile) {
        String key = "app" + RedisConstant.code_check + mobile;
        String redisCode = cacheService.get(key);
        ValidUtils.isFalseThrow(smscode.equals(redisCode), "验证码不正确，请查证后再试");
        cacheService.del(key);
    }
}
