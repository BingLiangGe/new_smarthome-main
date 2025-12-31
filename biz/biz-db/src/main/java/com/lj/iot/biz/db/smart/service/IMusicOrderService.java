package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.MusicOrderPageDto;
import com.lj.iot.biz.base.vo.MusicOrderPageVo;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 
 * 音乐畅听卡订单表 服务类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IMusicOrderService extends IService<MusicOrder> {

    IPage<MusicOrderPageVo> customPage(MusicOrderPageDto pageDto);

}
