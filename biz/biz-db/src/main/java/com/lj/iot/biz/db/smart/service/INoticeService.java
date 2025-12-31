package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.NoticePageDto;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.Product;

/**
 * <p>
 * 外卖订单表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
public interface INoticeService extends IService<Notice> {

    IPage<Notice> customPage(NoticePageDto pageDto,Long hotelId,String userId);

    Long unHandle(NoticePageDto pageDto,Long hotelId,String userId);
}
