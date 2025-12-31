package com.lj.iot.api.app.aop;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeLogAspect {

    @Around(value = "execution(public * com.lj.iot.api.app.web.auth.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        Object[] args = joinPoint.getArgs();
        //上传文件格式报错
        try {
            log.info(className + "." + methodName + ".params" + JSON.toJSONString(args) + "======" + time);
        }catch (Exception e){
            log.info(className + "." + methodName + ".params"  +args+ "===JSON格式异常===" + time);
        }
        return result;
    }
}
