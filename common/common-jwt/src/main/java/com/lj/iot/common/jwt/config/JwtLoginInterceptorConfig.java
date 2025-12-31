package com.lj.iot.common.jwt.config;

import com.lj.iot.common.jwt.interceptor.JwtLoginInterceptor;
import com.lj.iot.common.jwt.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 拦截器配置
 */
@Configuration
@ConditionalOnClass({JwtLoginInterceptor.class})
@Slf4j
public class JwtLoginInterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JwtProperties jwtConfigDto;

    public JwtLoginInterceptorConfig() {
    }

    @Bean
    public JwtLoginInterceptor jwtInterceptor() {
        log.info("==============================JWT开启===============================");
        return new JwtLoginInterceptor(this.jwtConfigDto.getTokenName(), this.jwtConfigDto.getJwtCCCFDFKey(), this.jwtConfigDto.getUidAesCCCFDFKey());
    }

    public void addInterceptors(InterceptorRegistry registry) {
        String[] ex = this.jwtConfigDto.getExcludePathPatterns() == null ? new String[]{""} : this.jwtConfigDto.getExcludePathPatterns();
        registry.addInterceptor(this.jwtInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger**/**", "/api/open/*")
                .excludePathPatterns(ex);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

