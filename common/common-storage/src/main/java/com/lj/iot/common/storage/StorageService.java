package com.lj.iot.common.storage;

import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.storage.config.StorageProperties;
import com.lj.iot.common.storage.config.StorageConstant;
import com.lj.iot.common.storage.minio.MinioService;
import com.lj.iot.common.storage.oss.OssService;
import com.lj.iot.common.storage.vo.UploadRetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
@Slf4j
public class StorageService {

    @Resource
    private StorageProperties storageProperties;
    @Resource
    private MinioService minioService;
    @Resource
    private OssService ossService;

    @PostConstruct
    public void init() {
        log.info("==================================================================");
        log.info("==================================================================");
        log.info("==================================================================");
        log.info("init... storageProperties = {}", storageProperties);
        log.info("==================================================================");
        log.info("==================================================================");
        log.info("==================================================================");

        if(StorageConstant.PROVIDER_MINIO.equals(storageProperties.getProvider())) {
            minioService.init();
        }else if(StorageConstant.PROVIDER_OSS.equals(storageProperties.getProvider())) {
            ossService.init();
        }

    }

    public UploadRetVo uploadFile(MultipartFile file, String fileName) {

        if(StorageConstant.PROVIDER_MINIO.equalsIgnoreCase(storageProperties.getProvider())){
            return minioService.uploadFile(file,fileName);
        }else if(StorageConstant.PROVIDER_OSS.equalsIgnoreCase(storageProperties.getProvider())){
            return ossService.uploadFile(file,fileName);
        }
        throw new CommonException(-1,"未知云存储提供商!");
    }


    public boolean deleteFile(String fileId){

        if(StorageConstant.PROVIDER_MINIO.equalsIgnoreCase(storageProperties.getProvider())){
            return minioService.deleteFile(fileId);
        }else if(StorageConstant.PROVIDER_OSS.equalsIgnoreCase(storageProperties.getProvider())){
            return ossService.deleteFile(fileId);
        }
        throw new CommonException(-1,"未知云存储提供商!");

    }

}
