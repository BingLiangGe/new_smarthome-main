package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;

import java.util.List;

/**
 * 
 * 射频设备型号表 服务类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IRfModelService extends IService<RfModel> {

    IPage<RfModel> customPage(PageDto pageDto);

}
