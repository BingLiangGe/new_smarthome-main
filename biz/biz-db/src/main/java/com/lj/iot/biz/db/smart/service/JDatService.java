package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.JDat;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tyj
 * @since 2023-05-30
 */
public interface JDatService extends IService<JDat> {

    public List<JDat> getTestIrData(String kdId);

    public List<JDat> page(Integer pageIndex,Integer pageSize);
    public List<JDat> selectAoksJdata();
}
