package com.lj.iot.biz.base.convert;

import com.alibaba.fastjson.JSON;
import com.lj.iot.common.base.dto.ThingModelProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * @author zzy
 * @description 自定义时间参数入参转换类
 * @date 2021年06月25日 11:09
 */
@Configuration
public class ThingModelPropertyConverter {
    @Bean
    public Converter<String, ThingModelProperty> string2ThingModelPropertyConverter() {
        return new Converter<String, ThingModelProperty>() {
            @Override
            public ThingModelProperty convert(String source) {
                return JSON.parseObject(source, ThingModelProperty.class);
            }
        };
    }
}

