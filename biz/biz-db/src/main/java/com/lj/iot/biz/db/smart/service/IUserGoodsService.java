package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;

/**
 * <p>
 * 外卖商品表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
public interface IUserGoodsService extends IService<UserGoods> {

    void cutQuantity(Long id ,Integer value);

    IPage<UserGoods> customPage(PageDto pageDto, Long hotelId, String userId);



    IPage<SeachDeviceVo> customPageUserDevice(PageDto pageDto);
}
