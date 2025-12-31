package com.lj.iot.api.job.fegin;

import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.api.job.job.*;
import com.lj.iot.api.job.util.QuartzUtil;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.common.base.dto.HwScheduleParamDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.fegin.job.JobFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@RestController
public class JobFeignClientController implements JobFeignClient {

    @Resource
    private Scheduler scheduler;

    @Override
    public CommonResultVo<String> deleteSceneJob(IdDto dto) {
        final String groupName = QuartzUtil.sceneGroupName(dto.getId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.delete:{}", groupName, e);
            return CommonResultVo.FAILURE();

        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> saveSceneJob(SceneJobParamDto paramDto) {
        String jobName = QuartzUtil.sceneJobName(paramDto.getId(), paramDto.getScheduleId());
        String triggerName = QuartzUtil.sceneTriggerName(paramDto.getId(), paramDto.getScheduleId());
        String groupName = QuartzUtil.sceneGroupName(paramDto.getId());

        final JobDetail job = JobBuilder.newJob(SceneJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_SCENE_ID, paramDto.getId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, paramDto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(paramDto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.save:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> addSchedule(ScheduleParamDto dto) {
        String jobName = QuartzUtil.scheduleJobName(dto.getScheduleId(), dto.getDeviceId());
        String triggerName = QuartzUtil.scheduleTriggerName(dto.getScheduleId(), dto.getDeviceId());
        String groupName = QuartzUtil.scheduleGroupName(dto.getDeviceId());

        final JobDetail job = JobBuilder.newJob(ScheduleJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.addSchedule:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除设备所有调度
     *
     * @param dto
     * @return
     */
    @Override
    public CommonResultVo<String> deleteScheduleByDeviceId(IdStrDto dto) {
        final String groupName = QuartzUtil.scheduleGroupName(dto.getId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.delete:{}", groupName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> deleteScheduleByScheduleId(ScheduleParamDto dto) {
        String jobName = QuartzUtil.scheduleJobName(dto.getScheduleId(), dto.getDeviceId());
        String groupName = QuartzUtil.scheduleGroupName(dto.getDeviceId());

        try {
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (Exception e) {
            log.error("SceneJobChangeListener.delete:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> addClock(ScheduleParamDto dto) {
        String jobName = QuartzUtil.clockJobName(dto.getScheduleId(), dto.getDeviceId());
        String triggerName = QuartzUtil.clockTriggerName(dto.getScheduleId(), dto.getDeviceId());
        String groupName = QuartzUtil.clockGroupName(dto.getDeviceId());

        final JobDetail job = JobBuilder.newJob(ClockJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_CLOCK_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.addClock:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除主控所有调度
     *
     * @param dto
     * @return
     */
    @Override
    public CommonResultVo<String> deleteClockByDeviceId(IdStrDto dto) {
        final String groupName = QuartzUtil.clockGroupName(dto.getId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.delete:{}", groupName, e);
            return CommonResultVo.FAILURE();

        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> deleteClockByScheduleId(ScheduleParamDto dto) {
        String jobName = QuartzUtil.clockJobName(dto.getScheduleId(), dto.getDeviceId());
        String groupName = QuartzUtil.clockGroupName(dto.getDeviceId());

        try {
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (Exception e) {
            log.error("SceneJobChangeListener.delete:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }




//    @Override
//    public CommonResultVo<String> saveTopicJob(TopicJobParamDto paramDto) {
//        String jobName = paramDto.getTopic();
//        String triggerName = paramDto.getTopic();
//        String groupName = paramDto.getTopic();
//
//        final JobDetail job = JobBuilder.newJob(SceneJob.class)
//                .withIdentity(jobName, groupName)
//                //.usingJobData(QuartzUtil.KEY_OF_SCENE_ID, paramDto.getId())
//                //.usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, paramDto.getScheduleId())
//                .build();
//        final Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity(triggerName, groupName)
//                .withSchedule(CronScheduleBuilder.cronSchedule(paramDto.getCron()))
//                .build();
//        try {
//            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
//            if (existsJob == null) {
//                scheduler.scheduleJob(job, trigger);
//            } else {
//                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
//            }
//        } catch (Exception e) {
//            log.error("SceneJobChangeListener.save:{}", jobName, e);
//            return CommonResultVo.FAILURE();
//        }
//        return CommonResultVo.SUCCESS();
//    }

    @Override
    public CommonResultVo<String> cancelOrderJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------cancelOrderJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.hwCancelOrderJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.hwCancelOrderTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.hwCancelOrderGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwCancelOrderJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_CANCEL_ORDER_NO, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.cancelOrderJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> completedHotelOrderJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------completedHotelOrderJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.hwCompletedHotelOrderJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.hwCompletedHotelOrderTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.hwCompletedHotelOrderGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwCompletedHotelOrderJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_COMPLETED_ORDER_NO, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.completedHotelOrderJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> room15MinSendVoiceJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------room15MinSendVoiceJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.hwRoom15MinSendVoiceJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.hwRoom15MinSendVoiceTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.hwRoom15MinSendVoiceGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwRoomSendVoiceJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_ROOM_15_MIN_SEND_VOICE, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.room15MinSendVoiceJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> room5MinSendVoiceJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------room5MinSendVoiceJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.hwRoom5MinSendVoiceJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.hwRoom5MinSendVoiceTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.hwRoom5MinSendVoiceGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwRoomLastFiveMinSendVoiceJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_ROOM_5_MIN_SEND_VOICE, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.room15MinSendVoiceJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> deleteHotelWechatCancelOrderJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------deleteHotelWechatCancelOrderJob------dto:" + JSONObject.toJSONString(dto));
        final String groupName = QuartzUtil.hwCancelOrderGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJob:{}", groupName, e);
            return CommonResultVo.FAILURE();

        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> lastReminderSendVoiceJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------lastReminderSendVoiceJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.lastReminderSendVoiceJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.lastReminderSendVoiceTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.lastReminderSendVoiceGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwLastReminderSendVoiceJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_LAST_REMINDER_SEND_VOICE, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.lastReminderSendVoiceJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> closeDevicesJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------closeDevicesJob------dto:" + JSONObject.toJSONString(dto));
        String jobName = QuartzUtil.closeDevicesJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.closeDevicesTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.closeDevicesGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwCloseDevicesJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_CLOSE_DEVICES, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.closeDevicesJobFail:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> deleteHotelWechatOrderJobs(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------deleteHotelWechatOrderJobs------dto:" + JSONObject.toJSONString(dto));
        final String openDevicesGroupName = QuartzUtil.openDevicesGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(openDevicesGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJobs:{}", openDevicesGroupName, e);
        }

        final String openContinuationOrdersDevicesGroupName = QuartzUtil.openContinuationOrdersDevicesGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(openContinuationOrdersDevicesGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJobs:{}", openContinuationOrdersDevicesGroupName, e);
        }

        final String completedHotelOrderGroupName = QuartzUtil.hwCompletedHotelOrderGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(completedHotelOrderGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJobs:{}", completedHotelOrderGroupName, e);
        }

        final String room15MinSendVoiceGroupName = QuartzUtil.hwRoom15MinSendVoiceGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(room15MinSendVoiceGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJob:{}", room15MinSendVoiceGroupName, e);
        }

        final String room5MinSendVoiceGroupName = QuartzUtil.hwRoom5MinSendVoiceGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(room5MinSendVoiceGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJob:{}", room5MinSendVoiceGroupName, e);
        }

        final String lastReminderSendVoiceGroupName = QuartzUtil.lastReminderSendVoiceGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(lastReminderSendVoiceGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJob:{}", lastReminderSendVoiceGroupName, e);
        }

        final String closeDevicesGroupName = QuartzUtil.closeDevicesGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(closeDevicesGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteHotelWechatOrderJob:{}", closeDevicesGroupName, e);
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> openDevicesJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------openDevicesJob------dto:" + JSONObject.toJSONString(dto));

        String jobName = QuartzUtil.openDevicesJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.openDevicesTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.openDevicesGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwOpenDevicesJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_OPEN_DEVICES, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.openDevicesJob:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> openContinuationOrdersDevicesJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------openContinuationOrdersDevicesJob------dto:" + JSONObject.toJSONString(dto));

        String jobName = QuartzUtil.openContinuationOrdersDevicesJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.openContinuationOrdersDevicesTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.openContinuationOrdersDevicesGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwOpenContinuationOrdersDevicesJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_OPEN_CONTINUATION_ORDERS_DEVICES, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.openContinuationOrdersDevicesJob:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> createRoomServiceJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------createRoomServiceJob------dto:" + JSONObject.toJSONString(dto));

        String jobName = QuartzUtil.createRoomServiceJobName(dto.getBusinessId(), dto.getScheduleId().toString());
        String triggerName = QuartzUtil.createRoomServiceTriggerName(dto.getBusinessId(), dto.getScheduleId().toString());
        String groupName = QuartzUtil.createRoomServiceGroupName(dto.getBusinessId());

        final JobDetail job = JobBuilder.newJob(HwCreateRoomServiceJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(QuartzUtil.KEY_OF_ORDER_ROOM_SERVICE_JOB, dto.getBusinessId())
                .usingJobData(QuartzUtil.KEY_OF_SCHEDULE_ID, dto.getScheduleId())
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(dto.getCron()))
                .build();
        try {
            final JobDetail existsJob = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (existsJob == null) {
                scheduler.scheduleJob(job, trigger);
            } else {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, groupName), trigger);
            }
        } catch (Exception e) {
            log.error("HwJobFeignClientController.createRoomServiceJob:{}", jobName, e);
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> deleteRoomServiceJob(HwScheduleParamDto dto) {
        log.error("----HwJobFeignClientController--------deleteRoomServiceJob------dto:" + JSONObject.toJSONString(dto));
        final String createRoomServiceGroupName = QuartzUtil.createRoomServiceGroupName(dto.getBusinessId());
        try {
            final Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(createRoomServiceGroupName));
            for (JobKey jobKey : jobKeys) {
                scheduler.deleteJob(jobKey);
            }
        } catch (Exception e) {
            log.error("SceneJobChangeListener.deleteRoomServiceJob:{}", createRoomServiceGroupName, e);
        }
        return CommonResultVo.SUCCESS();
    }
}