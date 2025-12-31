package com.lj.iot.api.job.job;

import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.api.job.service.BizJobService;
import com.lj.iot.api.job.util.QuartzUtil;
import com.lj.iot.common.base.dto.SendVoiceDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.feign.hotelwechat.HotelWechatApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@Component
public class HwOpenContinuationOrdersDevicesJob extends AbstractApplicationContextJob {
    @Autowired
    private BizJobService bizJobService;

    @Resource
    private HotelWechatApiFeignClient hotelWechatApiFeignClient;

    @Override
    public void execute(JobExecutionContext context) {
        final JobDetail jobDetail = context.getJobDetail();
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String orderNo = jobDataMap.getString(QuartzUtil.KEY_OF_OPEN_CONTINUATION_ORDERS_DEVICES);
        SendVoiceDto sendVoiceDto = SendVoiceDto.builder().code("16").orderNo(orderNo).build();

        CommonResultVo<String> commonResultVo = hotelWechatApiFeignClient.openContinuationOrdersDevices(sendVoiceDto);
        log.error("-----Hw Open Continuation Orders Devices Job-------execute-----sendVoiceDto={},commonResultVo={}",
                JSONObject.toJSONString(sendVoiceDto), JSONObject.toJSONString(commonResultVo));
        if (CommonCodeEnum.SUCCESS.getCode().equals(commonResultVo.getCode())) {
            bizJobService.deleteJob(jobDetail.getKey());
        }
    }
}
