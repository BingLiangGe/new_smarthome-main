package com.lj.iot.api.job.job;

import com.alibaba.fastjson.JSON;
import com.lj.iot.api.job.service.BizJobService;
import com.lj.iot.api.job.util.QuartzUtil;
import com.lj.iot.biz.base.dto.SceneJobTriggerDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author mz
 * @Date 2022/8/12
 * @since 1.0.0
 */
@Slf4j
public class SceneJob extends AbstractApplicationContextJob {

    @Resource
    private AppApiFeignClient appApiFeignClient;
    @Autowired
    private BizJobService bizJobService;

    @Override
    public void execute(JobExecutionContext context) {
        final JobDetail jobDetail = context.getJobDetail();
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        Long sceneId = jobDataMap.getLong(QuartzUtil.KEY_OF_SCENE_ID);
        log.info("KEY_OF_SCHEDULE_ID={}",QuartzUtil.KEY_OF_SCHEDULE_ID);
        Long scheduleId = jobDataMap.getLong(QuartzUtil.KEY_OF_SCHEDULE_ID);
        log.info("SceneJob.execute{}", JSON.toJSONString(jobDetail));

        CommonResultVo<String> commonResultVo = appApiFeignClient.trigger(SceneJobTriggerDto.builder()
                .sceneId(sceneId)
                .scheduleId(scheduleId)
                .build());
        if (CommonCodeEnum.NOT_EXIST.getCode().equals(commonResultVo.getCode())) {
            bizJobService.deleteJob(jobDetail.getKey());
        }
    }
}
