package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 设备出厂表 Mapper 接口
 *
 * @author xm
 * @since 2022-07-20
 */
public interface DeviceMapper extends BaseMapper<Device> {

    @Select("SELECT d.`id` FROM device d \n" +
            "LEFT JOIN user_device du ON du.device_id=d.id WHERE du.`device_id` IS NULL AND d.product_id = 213350486;")
    List<String> findNotBindDevice();

    @Select("SELECT uc.device_id FROM user_device uc \n" +
            "LEFT JOIN hotel_user_account hua ON uc.`user_id`=hua.`id`  WHERE uc.master_product_id =#{productId} AND uc.`hard_ware_version`!='1.1'")
    List<String> findUserAcccountDeviceNotHotel(Integer productId);

    IPage<DevicePageVo> customPage(IPage<DevicePageVo> page, @Param("params") PageDto pageDto);

    IPage<DevicePageVo> NewCustomPage(IPage<DevicePageVo> page, @Param("params") OtaDeviceIdPageDto pageDto);

    Integer activation(@Param("params") PageDto pageDto);

    Integer unActivation(@Param("params") PageDto pageDto);

    Integer newActivation(@Param("params") OtaDeviceIdPageDto pageDto);

    Integer newUnActivation(@Param("params") OtaDeviceIdPageDto pageDto);
}
