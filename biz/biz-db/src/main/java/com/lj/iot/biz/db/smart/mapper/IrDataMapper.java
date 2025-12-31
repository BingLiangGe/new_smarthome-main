package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.IrData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.entity.JDat;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
public interface IrDataMapper extends BaseMapper<IrData> {


    List<IrData> getDjAirContData(String fileIds);
    @Select("select * from `ir_model` where device_type_id=1 and brand_id=125 and file_type in('1','2')")
    List<IrModel> getIrDataDjiAirContr();
}
