package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.FenceNotice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tyj
 * @since 2023-10-23
 */
public interface IFenceNoticeService extends IService<FenceNotice> {

    Integer selectTodayNumber(String deviceId,Integer type);
}
