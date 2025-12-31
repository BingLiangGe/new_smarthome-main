package com.lj.iot.api.hotel.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class TimeLogAspect {

    /** 以 controller 包下定义的所有请求为切入点 */
    @Pointcut(value = "execution(public * com.lj.iot.api.hotel.web.auth.*.*(..))")
    public void appLog() {}

    @Pointcut(value = "execution(public * com.lj.iot.api.hotel.web.open.*.*(..))")
    public void appOpenLog() {}

    @Before("appLog()")
    public void doBefore(JoinPoint joinPoint){
        before(joinPoint);
    }
    @Around("appLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return around(proceedingJoinPoint);
    }
    @AfterThrowing(value = "appLog()",throwing = "ex")
    public void afterThrowing(Exception ex){
        after(ex);
    }

    @Before("appOpenLog()")
    public void doBeforeAppOpen(JoinPoint joinPoint){
        before(joinPoint);
    }
    @Around("appOpenLog()")
    public Object doAroundAppOpen(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return around(proceedingJoinPoint);
    }
    @AfterThrowing(value = "appOpenLog()",throwing = "ex")
    public void afterThrowingAppOpen(Exception ex){
        after(ex);
    }

    private void before(JoinPoint joinPoint){
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();

       /* // 打印请求相关参数
        log.info("========================================== Start ==========================================");
        // 打印请求 url
        log.info("URL            : {} ,{} ,args = {}", request.getRequestURL(), request.getMethod(),joinPoint.getArgs());*/
    }

    private Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return result;
        }
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        /*// 打印请求 url,返回的时候也打印下url,好对应到start,有时候耗时长的接口,start和end之间隔太远不好对应起来
        log.info("URL            : {} method={}.{}  params={} Time-Consuming : {} ms",
                request.getRequestURL(),className,methodName,joinPoint.getArgs(),
                System.currentTimeMillis() - startTime);

        //耗时接口单独打印
        if (System.currentTimeMillis() - startTime > 1000) {
            log.warn("Time-Consuming  USE TOO MUCH TIME !!!  used {}  ms,url={} ,args = {}",
                    System.currentTimeMillis() - startTime, request.getRequestURL(),joinPoint.getArgs());
        }

        log.info("=========================================== End ===========================================");
        // 每个请求之间空一行
        log.info("");*/

        return result;
    }

    private void after(Exception  ex){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return ;
        }
        HttpServletRequest request = attributes.getRequest();
        log.error("URL            : {} catch Exception {} msg={}!!!!", request.getRequestURL(),ex.getClass().getName(),ex.getMessage());
        //ex.printStackTrace();
        log.info("=========================================== End ===========================================");
        // 每个请求之间空一行
        log.info("");
    }
}