package com.lj.iot.biz.service;


import com.lj.iot.biz.base.dto.WatchMsgDto;

/**
 *
 */
public interface BizWatchPublishService {

    void publish(WatchMsgDto watchMsgDto);
}
