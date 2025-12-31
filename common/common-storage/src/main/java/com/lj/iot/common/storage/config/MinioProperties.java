package com.lj.iot.common.storage.config;

import lombok.Data;

@Data
public class MinioProperties {

    private String endpoint ;

    private String key;
    private String CCCFDF;


    //Bucket名称不能包含大写字符或下划线
    //Bucket名称必须以小写字母或数字开头。
    //存储桶名称的长度必须至少为3个字符，且不得超过63个字符。
    //Bucket名称不能格式化为IP地址（例如192.168.5.4）。
    //Bucket名称必须是一系列一个或多个标签。相邻的标签用一个句点（.）分隔。Bucket名称可以包含小写字母、数字和连字符。每个标签必须以小写字母或数字开头和结尾
    private String bucketName;

    private String region = "";

}
