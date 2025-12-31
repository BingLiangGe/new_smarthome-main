package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 外卖商品表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
public interface UserGoodsMapper extends BaseMapper<UserGoods> {

    boolean cutQuantity(@Param("id") Long id, @Param("value") Integer value);

    IPage<SeachDeviceVo> customPageUserDevice(IPage<UserGoods> page, @Param("params") PageDto pageDto);

    IPage<UserGoods> customPage(IPage<UserGoods> page, @Param("params") PageDto pageDto, @Param("hotelId") Long hotelId, @Param("userId") String userId);
}
