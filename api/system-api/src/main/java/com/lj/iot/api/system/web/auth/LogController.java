package com.lj.iot.api.system.web.auth;


import com.alibaba.fastjson.JSON;
import com.lj.iot.biz.base.dto.DeviceOtaDto;
import com.lj.iot.biz.base.dto.SceneJobParamDto;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.fegin.job.JobFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * OTA
 *
 * @author hao
 * @Date 2023/2/17
 * @since 1.0.0
 */
@Slf4j
@RestController
@EnableScheduling
public class LogController {

    @Autowired
    private IOperationLogService iOperationLogService;

    @Autowired
    private JobFeignClient jobFeignClient;

    @Scheduled(cron ="5 0/1 * * * ?")
    public CommonResultVo<String> task() {
        //查询出不在线的
        ArrayList<OperationLog> task = iOperationLogService.task();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < task.size(); i++) {
            task.get(i).setStatus(new Byte("0"));
            task.get(i).setAction(new Byte("0"));
            task.get(i).setRemark("thing/event/topology/line");
            task.get(i).setCreateTime(now);
        }
        log.info("task:===========离线数量:"+task.size());
        //iOperationLogService.saveBatch(task);
        return CommonResultVo.SUCCESS();
    }


}
