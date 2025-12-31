package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.NoticePageDto;
import com.lj.iot.biz.db.smart.entity.Notice;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 外卖订单表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    IPage<Notice> customPage(IPage<Notice> page, @Param("params") NoticePageDto pageDto, @Param("hotelId") Long hotelId, @Param("userId") String userId);

    Long unHandle(@Param("params") NoticePageDto pageDto, @Param("hotelId") Long hotelId, @Param("userId") String userId);
}
