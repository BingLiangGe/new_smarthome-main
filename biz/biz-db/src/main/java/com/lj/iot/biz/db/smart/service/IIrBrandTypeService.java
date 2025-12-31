package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.BrandTypeVo;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
public interface IIrBrandTypeService extends IService<IrBrandType> {

    List<BrandTypeVo> listByDeviceTypeId(Long deviceTypeId);
}
