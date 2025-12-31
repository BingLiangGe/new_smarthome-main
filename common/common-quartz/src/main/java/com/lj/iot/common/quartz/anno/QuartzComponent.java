package com.lj.iot.common.quartz.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface QuartzComponent {

    String cron() default "";

    /**
     * 间隔执行【毫秒】
     *
     * @return
     */
    long fixedRate() default -1L;

    /**
     * 是否支持并发，默认不支持
     *
     * @return
     */
    boolean concurrent() default false;
}
