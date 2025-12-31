package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.dto.RfBrandAddDto;
import com.lj.iot.biz.base.dto.RfBrandEditDto;
import com.lj.iot.biz.db.smart.entity.RfBrand;

import java.util.List;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizRfBrandService {

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    IPage<RfBrand> page(PageDto pageDto);

    List<RfBrand> listByDeviceTypeId(Long deviceTypeId);

    /**
     * 新增
     *
     * @param paramDto
     */
    void add(RfBrandAddDto paramDto);

    /**
     * 修改
     *
     * @param paramDto
     */
    void edit(RfBrandEditDto paramDto);

    /**
     * 删除
     *
     * @param paramDto
     */
    void delete(IdDto paramDto);
}
