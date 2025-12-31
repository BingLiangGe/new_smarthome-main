package com.lj.iot.biz.base.convert;

import com.alibaba.fastjson.JSON;
import com.lj.iot.common.base.dto.ThingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * @author zzy
 * @description 自定义时间参数入参转换类
 * @date 2021年06月25日 11:09
 */
@Configuration
public class ThingModelConverter {
    @Bean
    public Converter<String, ThingModel> string2ThingModelConverter() {
        return new Converter<String, ThingModel>() {
            @Override
            public ThingModel convert(String source) {
                System.out.println(source);
                return JSON.parseObject(source, ThingModel.class);
            }
        };
    }
}

