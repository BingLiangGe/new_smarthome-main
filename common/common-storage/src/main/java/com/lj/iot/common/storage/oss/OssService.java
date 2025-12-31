package com.lj.iot.common.storage.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.storage.config.StorageConstant;
import com.lj.iot.common.storage.config.StorageProperties;
import com.lj.iot.common.storage.vo.UploadRetVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * @author ykl
 * @since 2023/11/9
 */
@Service
public class OssService {

    @Resource
    private StorageProperties storageProperties;

    private OSSClient ossClient;

    public void init() {
        try {

            ossClient = new OSSClient(storageProperties.getOss().getEndpoint(), storageProperties.getOss().getKey(), storageProperties.getOss().getCCCFDF());
            ossClient.setBucketCORS(getCrossRequest());

        } catch (Exception e) {
            e.printStackTrace();
            ossClient = null;
            //throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "初始化OssClient失败!");
        }
    }

    public UploadRetVo uploadFile(MultipartFile file, String fileName) {

        try {

            if(ossClient == null){
                init();
            }
            if (null == file || 0 == file.getSize()) {
                throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "文件为空!");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(Calendar.getInstance().getTimeInMillis());

            String suffix;
            if(file.getOriginalFilename() !=null && file.getOriginalFilename().lastIndexOf(".") > 0){
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            }else{
                suffix = "temp";
            }
            if (StringUtils.isBlank(fileName) || file.getOriginalFilename() == null) {
                fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
            }
            String  fileId  = String.format("public/%s/%s/%s",storageProperties.getEnvName(),dateStr,fileName);

            ObjectMetadata customHeaders = new ObjectMetadata();
            if(file.getContentType() != null){
                customHeaders.setContentType(file.getContentType());
            }
            ossClient.putObject(storageProperties.getOss().getBucketName(),fileId,file.getInputStream(), customHeaders);

            return UploadRetVo.builder()
                    .url(String.format("%s/%s",storageProperties.getOss().getDomainUrl(),fileId))
                    .fileId(fileId)
                    .provider(StorageConstant.PROVIDER_OSS)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "上传文件失败!");
        }
    }

    public boolean deleteFile(String fileId) {

        try{
            if(ossClient == null){
                init();
            }
            ossClient.deleteObject(storageProperties.getOss().getBucketName(),fileId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    private SetBucketCORSRequest getCrossRequest() {

        SetBucketCORSRequest request = new SetBucketCORSRequest(storageProperties.getOss().getBucketName());
        // 跨域资源共享规则的容器，每个存储空间最多允许10条规则。
        ArrayList<SetBucketCORSRequest.CORSRule> putCorsRules = new ArrayList<SetBucketCORSRequest.CORSRule>();

        SetBucketCORSRequest.CORSRule corRule = new SetBucketCORSRequest.CORSRule();

        ArrayList<String> allowedOrigin = new ArrayList<String>();
        // 指定允许跨域请求的来源。
        allowedOrigin.add("*");
        ArrayList<String> allowedMethod = new ArrayList<String>();

        // 指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD)。
        allowedMethod.add("GET");
        allowedMethod.add("PUT");
        allowedMethod.add("POST");
        allowedMethod.add("HEAD");
        allowedMethod.add("DELETE");

        corRule.setAllowedMethods(allowedMethod);
        corRule.setAllowedOrigins(allowedOrigin);
        // AllowedHeaders和ExposeHeaders不支持通配符。
        //        corRule.setAllowedHeaders(allowedHeader);
        //        corRule.setExposeHeaders(exposedHeader);
        // 指定浏览器对特定资源的预取（OPTIONS）请求返回结果的缓存时间，单位为秒。
        corRule.setMaxAgeSeconds(10);

        // 最多允许10条规则。
        putCorsRules.add(corRule);
        // 已存在的规则将被覆盖。
        request.setCorsRules(putCorsRules);
        return request;
    }
}
