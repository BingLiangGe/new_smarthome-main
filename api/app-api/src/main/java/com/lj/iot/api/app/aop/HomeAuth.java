package com.lj.iot.api.app.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HomeAuth {
    String value() default "";

    PermType type() default PermType.MAIN;

    enum PermType {

        /**
         * 主账号
         */
        MAIN,

        /**
         * 可编辑子账号
         */
        EDIT,

        /**
         * 成员账号
         */
        MEMBER,

        /**
         * 成员加子账号
         */
        ALL
    }
}
