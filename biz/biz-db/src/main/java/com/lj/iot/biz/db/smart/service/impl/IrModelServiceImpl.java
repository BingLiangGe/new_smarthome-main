package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.mapper.IrModelMapper;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
@DS("smart")
@Service
public class IrModelServiceImpl extends ServiceImpl<IrModelMapper, IrModel> implements IIrModelService {

}
