package com.lj.iot.api.system.web.auth;

import cn.hutool.extra.ssh.JschRuntimeException;
import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.minio.service.MinioService;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/file")
public class AuthFileController {

    @Value("${minio.bucket-name:iot}")
    private String bucketName;

    @Resource
    private MinioService minioService;


    /**
     * 文件上传
     *
     * @return
     */
    @CustomPermissions("file:upload")
    @RequestMapping("/upload")
    public CommonResultVo<String> upload( @RequestParam("file") MultipartFile file) {
        JSONObject json = minioService.uploadFile(file, "", bucketName);
        return CommonResultVo.SUCCESS(json.getString("filePath"));
    }

}
