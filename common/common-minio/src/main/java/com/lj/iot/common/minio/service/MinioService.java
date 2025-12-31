package com.lj.iot.common.minio.service;

import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.minio.clientconfig.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
public class MinioService {
    private static final String PATH_SEPARATOR_CHAR = "/";
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public JSONObject uploadFile(MultipartFile file, String fileName, String bucketName) {
        //判断文件是否为空
        if (null == file || 0 == file.getSize() || StringUtils.isBlank(file.getOriginalFilename())) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "文件为空或文件名、文件格式不正确!");
        }

        String originalFilename;
        try {
            //判断存储桶是否存在  不存在则创建
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
            originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(fileName)) {
                //新的文件名 = 存储桶文件名_时间戳.后缀名
                fileName = UUID.randomUUID().toString().replace("-", "") +
                        originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                int lastIdx = fileName.lastIndexOf(PATH_SEPARATOR_CHAR);
                if (fileName.length() + 1 > lastIdx) {
                    fileName = fileName.substring(lastIdx + 1);
                }
            }

            //开始上传
            PutObjectOptions putObjectOptions = new PutObjectOptions(file.getSize(), -1);
            putObjectOptions.setContentType(file.getContentType());
            minioClient.putObject(bucketName, fileName, file.getInputStream(), putObjectOptions);

            BigDecimal sizeB = new BigDecimal(file.getSize());
            BigDecimal sizeC = new BigDecimal(1048576);
            BigDecimal fileSize = sizeB.divide(sizeC,2,RoundingMode.HALF_UP);
            JSONObject json = new JSONObject();
            json.put("fileName",  fileName);
            json.put("originalFileName",originalFilename);
            json.put("fileSize",fileSize);

            String filePath;
            if (minioProperties.getEndpoint().endsWith(PATH_SEPARATOR_CHAR)) {
                filePath = minioProperties.getEndpoint() + bucketName + PATH_SEPARATOR_CHAR + fileName;
            } else {
                filePath = minioProperties.getEndpoint() + PATH_SEPARATOR_CHAR + bucketName + PATH_SEPARATOR_CHAR + fileName;
            }
            json.put("filePath", filePath);
            return json;
        } catch (Exception e) {
            log.error("MinioUtils uploadFile error.", e);
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "上传失败!");
        }
    }

    /**
     * 删除图片
     * @param bucketName
     * @param objectName
     * @return
     * @throws IOException
     * @throws InvalidResponseException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InternalException
     * @throws XmlParserException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     */
    public boolean removeObject(String bucketName, String objectName) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, InternalException, XmlParserException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        boolean flag = minioClient.bucketExists(bucketName);
        if (!flag) {
            return false;
        }

        minioClient.removeObject(bucketName, objectName);
        return true;
    }
}
