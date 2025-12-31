package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.IrDeviceType;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizIrDeviceTypeService {

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    IPage<IrDeviceType> customPage(PageDto pageDto);
}
