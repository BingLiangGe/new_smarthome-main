package com.lj.iot.common.storage.minio;

import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.storage.config.StorageProperties;
import com.lj.iot.common.storage.config.MinioProperties;
import com.lj.iot.common.storage.config.StorageConstant;
import com.lj.iot.common.storage.vo.UploadRetVo;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Service
public class MinioService {

    private static final String PATH_SEPARATOR_CHAR = "/";
    private MinioClient minioClient;
    private MinioProperties minioProperties;

    @Resource
    private StorageProperties storageProperties;

    public void init() {
        try {
            minioProperties = storageProperties.getMinio();
            minioClient = new MinioClient(minioProperties.getEndpoint(),
                    minioProperties.getKey(),
                    minioProperties.getCCCFDF(),
                    minioProperties.getRegion());
            //判断存储桶是否存在  不存在则创建
            if (!minioClient.bucketExists(minioProperties.getBucketName())) {
                minioClient.makeBucket(minioProperties.getBucketName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "初始化MinioClient失败!");
        }
    }

    public UploadRetVo uploadFile(MultipartFile file, String fileName) {

        //判断文件是否为空
        if (null == file || 0 == file.getSize()) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "文件为空!");
        }

        try {
            if(minioClient == null){
                init();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(Calendar.getInstance().getTimeInMillis());
            String suffix;
            if (file.getOriginalFilename() != null && file.getOriginalFilename().lastIndexOf(".") > 0) {
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            } else {
                suffix = "temp";
            }
            if (StringUtils.isBlank(fileName) || file.getOriginalFilename() == null) {
                fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
            }
            String fileId = String.format("public/%s/%s/%s", storageProperties.getEnvName(), dateStr, fileName);

            PutObjectOptions putObjectOptions = new PutObjectOptions(file.getSize(), -1);
            if (file.getContentType() != null) {
                putObjectOptions.setContentType(file.getContentType());
            }
            minioClient.putObject(minioProperties.getBucketName(), fileId, file.getInputStream(), putObjectOptions);

            String filePath;
            if (minioProperties.getEndpoint().endsWith(PATH_SEPARATOR_CHAR)) {
                filePath = minioProperties.getEndpoint() + minioProperties.getBucketName() + PATH_SEPARATOR_CHAR + fileId;
            } else {
                filePath = minioProperties.getEndpoint() + PATH_SEPARATOR_CHAR + minioProperties.getBucketName() + PATH_SEPARATOR_CHAR + fileId;
            }
            return UploadRetVo.builder()
                    .url(filePath)
                    .fileId(fileId)
                    .provider(StorageConstant.PROVIDER_MINIO)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "上传文件失败!");
        }

    }


    public boolean deleteFile(String fileId) {

        try {
            if(minioClient == null){
                init();
            }
            minioClient.removeObject(minioProperties.getBucketName(), fileId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
