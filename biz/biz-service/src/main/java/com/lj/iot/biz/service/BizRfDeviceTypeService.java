package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.dto.RfDeviceTypeAddDto;
import com.lj.iot.biz.base.dto.RfDeviceTypeEditDto;
import com.lj.iot.biz.db.smart.entity.RfDeviceType;

import java.util.List;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizRfDeviceTypeService {

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    IPage<RfDeviceType> page(PageDto pageDto);

    /**
     * 根据品牌ID查询设备类型
     *
     * @param brandId
     * @return
     */
    List<RfDeviceType> listByBrandId(Long brandId);

    /**
     * 新增
     *
     * @param paramDto
     */
    void add(RfDeviceTypeAddDto paramDto);

    /**
     * 修改
     *
     * @param paramDto
     */
    void edit(RfDeviceTypeEditDto paramDto);

    /**
     * 删除
     *
     * @param paramDto
     */
    void delete(IdDto paramDto);
}
