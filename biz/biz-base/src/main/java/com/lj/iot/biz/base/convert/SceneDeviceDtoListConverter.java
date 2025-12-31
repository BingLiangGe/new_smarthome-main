package com.lj.iot.biz.base.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lj.iot.biz.base.dto.SceneDeviceDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author zzy
 * @description 自定义时间参数入参转换类
 * @date 2021年06月25日 11:09
 */
@Configuration
public class SceneDeviceDtoListConverter {
    @Bean
    public Converter<String, List<SceneDeviceDto>> string2SceneDeviceDtoList() {
        return new Converter<String, List<SceneDeviceDto>>() {
            @Override
            public List<SceneDeviceDto> convert(String source) {
                return JSON.parseObject(source, new TypeReference<List<SceneDeviceDto>>() {
                });
            }
        };
    }
}

