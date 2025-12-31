package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * 射频设备品牌表 服务类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IRfBrandService extends IService<RfBrand> {

    List<RfBrand> listByTypeId(Long rfDeviceTypeId);
}
