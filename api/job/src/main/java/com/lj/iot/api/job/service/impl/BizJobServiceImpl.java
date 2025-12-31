package com.lj.iot.api.job.service.impl;

import com.alibaba.fastjson.JSON;
import com.lj.iot.api.job.service.BizJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class BizJobServiceImpl implements BizJobService {

    @Resource
    private Scheduler scheduler;

    @Override
    public void deleteJob(JobKey jobKey) {
        try {
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("BizJobServiceImpl.deleteJob.jobKey:{}", JSON.toJSONString(jobKey), e);
        }
    }
}
