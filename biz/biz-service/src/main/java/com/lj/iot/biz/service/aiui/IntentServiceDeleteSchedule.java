package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import com.lj.iot.biz.service.BizUserDeviceScheduleService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.common.base.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 删除调度
 */
@Component("intentService_deleteSchedule")
public class IntentServiceDeleteSchedule implements IntentService {

    @Autowired
    private BizUserDeviceScheduleService bizUserDeviceScheduleService;

    /**
     * 插槽
     * <p>
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        IntentDto.Slot datetimeSlot = intentDto.getSlots().get("datetime");

        //没有说具体时间
        if (datetimeSlot == null) {

            if (intentDto.getSlots().get("range") != null) {
                bizUserDeviceScheduleService.deleteByDeviceId(masterUserDevice.getDeviceId());
                return;
            }

            List<UserDeviceSchedule> userDeviceScheduleList = bizUserDeviceScheduleService.listByMasterDeviceId(masterUserDevice.getDeviceId());
            if (userDeviceScheduleList.size() <= 1) {
                bizUserDeviceScheduleService.delete(userDeviceScheduleList);
                return;
            }

            //没有说所有，且匹配到多个时间点。提示用户
            String tip = "删除所有任务";
            Set<String> set = new HashSet<>();
            for (UserDeviceSchedule userDeviceSchedule : userDeviceScheduleList) {
                if (!set.contains(userDeviceSchedule.getRemark())) {
                    tip = tip + "、或者" + "删除" + userDeviceSchedule.getRemark() + "任务";
                    set.add(userDeviceSchedule.getRemark());
                }
            }
            String template = "匹配到多个任务" + ",你可以说：" + tip;
            throw CommonException.FAILURE(template);
        }
        String cron = DatetimeSlotUtil.legalCron(datetimeSlot);
        bizUserDeviceScheduleService.deleteByDeviceIdAndCron(masterUserDevice.getMasterDeviceId(), cron, datetimeSlot.getValue());
    }
}
