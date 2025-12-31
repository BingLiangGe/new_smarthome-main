package com.lj.iot.common.quartz.service.impl;

import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.quartz.service.IQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class QuartzServiceImpl implements IQuartzService {

    @Resource
    private Scheduler scheduler;

    @Override
    public void addJob(String jobName, String jobGroup, String cron, JobDataMap dataMap, Class<Job> jobClass) {
        try {
            //创建job，指定job名称和分组
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
            //创建表达式工作计划
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            //创建触发器
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                    .withSchedule(cronScheduleBuilder)
                    .usingJobData(dataMap)
                    .build();
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (Exception e) {
            log.error("QuartzServiceImpl.startJob", e);
            throw CommonException.FAILURE("执行任务调度出错");
        }
    }

    @Override
    public void modifyJob(String jobName, String jobGroup, String cron, JobDataMap dataMap) {
        try {
            TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldCron = trigger.getCronExpression();
            if (!oldCron.equals(cron)) {
                CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                        .usingJobData(dataMap)
                        .build();
                Date date = scheduler.rescheduleJob(triggerKey, cronTrigger);
                if (date == null) {
                    throw new Exception("修改定时任务执行时间报错");
                }
            }
        } catch (Exception e) {
            log.error("QuartzServiceImpl.modifyJob", e);
            throw CommonException.FAILURE("修改任务调度出错");
        }
    }

    @Override
    public void pauseJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            if (jobKey == null) {
                return;
            }
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("QuartzServiceImpl.pauseJob", e);
            throw CommonException.FAILURE("暂停任务调度出错");
        }
    }

    @Override
    public void resumeJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            if (jobKey == null) {
                return;
            }
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            log.error("QuartzServiceImpl.resumeJob", e);
            throw CommonException.FAILURE("恢复任务调度出错");
        }
    }

    @Override
    public void deleteJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            if (jobKey == null) {
                return;
            }
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("QuartzServiceImpl.deleteJob", e);
            throw CommonException.FAILURE("删除任务调度出错");
        }
    }

    private JobKey getJobKey(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return null;
        }
        return jobKey;
    }
}
