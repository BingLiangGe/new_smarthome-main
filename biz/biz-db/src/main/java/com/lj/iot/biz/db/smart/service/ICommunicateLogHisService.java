package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.CommunicateLogHisPageDto;
import com.lj.iot.biz.base.dto.HomeIdPageDto;
import com.lj.iot.biz.db.smart.entity.CommunicateLogHis;
import com.lj.iot.common.base.dto.PageDto;

import java.util.List;

/**
 * 历史通话记录 服务类
 *
 * @author xm
 * @since 2022-07-19
 */
public interface ICommunicateLogHisService extends IService<CommunicateLogHis> {

    IPage<CommunicateLogHis> customPage(PageDto pageDto, Long homeId, String userId);
}
