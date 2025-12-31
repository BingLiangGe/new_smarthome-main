package com.lj.iot.common.storage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ykl
 * @since 2023/11/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("上传结果")
public class UploadRetVo {

    @ApiModelProperty("上传完成后文件访问地址")
    private String url;

    @ApiModelProperty("文件id")
    private String fileId;

    @ApiModelProperty("云存储提供者 oss,minio")
    private String provider;

}
