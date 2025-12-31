package com.lj.iot.api.job.service;

import org.quartz.JobKey;

public interface BizJobService {

    void deleteJob(JobKey jobKey);
}
