package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.JDat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-05-30
 */
public interface JDatMapper extends BaseMapper<JDat> {


    @Select("SELECT * FROM j_dat WHERE tags LIKE '%${kdId}%'")
    public List<JDat> getTestIrData(String kdId);

    @Select("select * from j_dat limit #{pageIndex},#{pageSize}")
    public List<JDat> page(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);


    @Select("SELECT * FROM j_dat WHERE tags IN \n" +
            "('010167_0-0-0-0-0-0','010167_0-0-1-0-0-0','010167_0-0-2-0-0-0','010167_0-0-3-0-0-0','010167_0-0-4-0-0-0','010167_0-0-5-0-0-0','010167_0-0-6-0-0-0'\n" +
            ",'010167_0-0-7-0-0-0','010167_0-0-8-0-0-0','010167_0-0-9-0-0-0','010167_0-0-10-0-0-0','010167_0-0-11-0-0-0','010167_0-0-12-0-0-0','010167_0-0-13-0-0-0'\n" +
            ",'010167_0-0-14-0-0-0','010167_0-0-15-0-0-0')\n")
    public List<JDat> selectAoksJdata();
}
