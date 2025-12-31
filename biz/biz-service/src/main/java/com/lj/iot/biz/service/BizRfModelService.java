package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.common.base.dto.PageDto;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizRfModelService {

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    IPage<RfModel> page(PageDto pageDto);

    /**
     * 新增
     *
     * @param paramDto
     */
    void add(RfModelAddDto paramDto);

    /**
     * 修改
     *
     * @param paramDto
     */
    void edit(RfModelEditDto paramDto);

    /**
     * 删除
     *
     * @param paramDto
     */
    void delete(IdDto paramDto);
}
