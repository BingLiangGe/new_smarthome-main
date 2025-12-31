package com.lj.iot.api.hotel.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPermissions {
    String value() default "";
}
