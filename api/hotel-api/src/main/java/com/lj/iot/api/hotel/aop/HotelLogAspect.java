/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.api.hotel.aop;

import com.alibaba.fastjson.JSON;
import com.lj.iot.biz.db.smart.entity.HotelLog;
import com.lj.iot.biz.db.smart.service.IHotelLogService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.util.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * 酒店日志，切面处理类
 *
 * @author Mark sunlightcs@gmail.com
 */
@Aspect
@Component
public class HotelLogAspect {
    @Autowired
    private IHotelLogService hotelLogService;

    @Pointcut("@annotation(com.lj.iot.api.hotel.aop.HotelLogAop)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        HotelLog hotelLog = new HotelLog();
        hotelLog.setHotelId(UserDto.getUser().getHotelId());
        HotelLogAop sysLogAop = method.getAnnotation(HotelLogAop.class);
        if (sysLogAop != null) {
            //注解上的描述
            hotelLog.setOperation(sysLogAop.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        hotelLog.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSON.toJSONString(args);
            hotelLog.setParams(params);
        } catch (Exception e) {

        }

        //设置IP地址
        hotelLog.setIp(IPUtils.getIp());

        //用户名
        String username = UserDto.getUser().getAccount();
        hotelLog.setUsername(username);

        hotelLog.setTime(time);
        //保存系统日志
        hotelLogService.save(hotelLog);
    }
}
