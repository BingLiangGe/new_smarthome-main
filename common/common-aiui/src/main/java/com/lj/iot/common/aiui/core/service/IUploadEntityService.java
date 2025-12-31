package com.lj.iot.common.aiui.core.service;

import com.lj.iot.common.aiui.core.dto.UploadEntityDto;

/**
 * 上传实体
 *
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
public interface IUploadEntityService {

    /**
     * 自定义
     *
     * @param uploadEntityDto
     * @return
     */
    Boolean uploadCustomLevel(UploadEntityDto uploadEntityDto);


    /**
     * 自定义场景
     *
     * @param uploadEntityDto
     * @return
     */
    Boolean uploadCustomLevelTrigger(UploadEntityDto uploadEntityDto);

    /**
     * 用户级
     *
     * @param uploadEntityDto
     * @return
     */
    Boolean uploadUserLevel(UploadEntityDto uploadEntityDto);

    /**
     * 应用级
     *
     * @param uploadEntityDto
     * @return
     */
    Boolean uploadAppLevel(UploadEntityDto uploadEntityDto);

    Boolean check(String sid);
}
