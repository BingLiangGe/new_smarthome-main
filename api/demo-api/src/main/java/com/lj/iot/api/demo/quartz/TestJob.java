/*
package com.lj.iot.api.demo.quartz;

import com.lj.iot.common.quartz.anno.QuartzComponent;
import com.lj.iot.common.redis.service.ICacheService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@QuartzComponent(cron = "0/5 * * * * ?")
public class TestJob implements Job {

    @Autowired
    private ICacheService cacheService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        cacheService.convertAndSend("topic1","测试JOB");

    }
}
*/
