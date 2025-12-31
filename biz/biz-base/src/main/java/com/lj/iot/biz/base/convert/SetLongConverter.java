package com.lj.iot.biz.base.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.Set;

/**
 * @author zzy
 * @description 自定义时间参数入参转换类
 * @date 2021年06月25日 11:09
 */
@Configuration
public class SetLongConverter {
    @Bean
    public Converter<String, Set<Long>> string2SetLongConverter() {
        return new Converter<String, Set<Long>>() {
            @Override
            public Set<Long> convert(String source) {
                return JSON.parseObject(source, new TypeReference<Set<Long>>() {
                });
            }
        };
    }
}

