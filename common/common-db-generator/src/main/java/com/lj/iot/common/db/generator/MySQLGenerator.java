package com.lj.iot.common.db.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class MySQLGenerator {


    private static final String url = "jdbc:mysql://47.100.238.205:3308/new_iot?useSSL=false&useUnicode=true&characterEncoding=UTF-8&pinGlobalTxToPhysicalConnection=true";
    private static final String userName = "root";
    private static final String password = "LjWl123456";
    private static final String author = "tyj";
    private static final String outputDir = "C:/Users/A/Desktop/mybatis_generator/code";
    private static final String parent = "com.lj.iot.biz.db";
    private static final String moduleName = "smart";

    private static final String xmlDir = "C:/Users/A/Desktop/mybatis_generator/code/mybatis/mapper" + (moduleName.trim().equals("") ? "" : "/" + moduleName);


    public static void main(String[] args) {

        FastAutoGenerator.create(url, userName, password)
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .outputDir(outputDir); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder
                            .moduleName(moduleName) // 设置父包模块名
                            .parent(parent) // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, xmlDir)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.mapperBuilder().enableBaseResultMap().enableBaseColumnList();

                    builder
                            //.addInclude("ir_brand,ir_brand_type,ir_device_type,ir_model,rf_brand,rf_brand_type,rf_device_type,rf_model") // 设置需要生成的表名
                            .addInclude("device_record") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

