package com.lj.iot.common.quartz.service;

import org.quartz.Job;
import org.quartz.JobDataMap;

public interface IQuartzService {

    /**
     * 添加
     *
     * @param jobName
     * @param cron
     * @param jobGroup
     * @param jobClass
     */
    void addJob(String jobName, String jobGroup, String cron, JobDataMap dataMap, Class<Job> jobClass);

    /**
     * 修改定时任务执行时间
     *
     * @param jobName
     * @param jobGroup
     * @param cron
     */
    void modifyJob(String jobName, String jobGroup, String cron, JobDataMap dataMap);

    /**
     * 暂停任务
     *
     * @param jobName
     * @param jobGroup
     */
    void pauseJob(String jobName, String jobGroup);

    /**
     * 恢复任务
     *
     * @param jobName
     * @param jobGroup
     */
    void resumeJob(String jobName, String jobGroup);

    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     */
    void deleteJob(String jobName, String jobGroup);
}
