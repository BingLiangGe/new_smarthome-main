package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.MusicOrderPageDto;
import com.lj.iot.biz.base.vo.MusicOrderPageVo;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 音乐畅听卡订单表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface MusicOrderMapper extends BaseMapper<MusicOrder> {

    IPage<MusicOrderPageVo> customPage(IPage<MusicOrder> page, @Param("params") MusicOrderPageDto pageDto);
}
