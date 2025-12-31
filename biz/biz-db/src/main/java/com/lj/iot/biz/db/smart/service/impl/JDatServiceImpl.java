package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.JDat;
import com.lj.iot.biz.db.smart.mapper.IrDataMapper;
import com.lj.iot.biz.db.smart.mapper.JDatMapper;
import com.lj.iot.biz.db.smart.service.IIrDataService;
import com.lj.iot.biz.db.smart.service.JDatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-05-30
 */
@DS("smart")
@Service
public class JDatServiceImpl extends ServiceImpl<JDatMapper, JDat> implements JDatService {

    @Resource
    private JDatMapper jDatMapper;

    @Override
    public List<JDat> getTestIrData(String kdId) {
        return jDatMapper.getTestIrData(kdId);
    }

    @Override
    public List<JDat> page(Integer pageIndex, Integer pageSize) {
        return jDatMapper.page(pageIndex,pageSize);
    }

    @Override
    public List<JDat> selectAoksJdata() {
        return jDatMapper.selectAoksJdata();
    }
}
