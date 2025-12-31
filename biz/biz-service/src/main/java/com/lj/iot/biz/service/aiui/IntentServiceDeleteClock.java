package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.service.BizClockService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.common.base.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 删除闹钟
 */
@Component("intentService_deleteClock")
public class IntentServiceDeleteClock implements IntentService {

    @Autowired
    private BizClockService bizClockService;

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
                bizClockService.deleteByDeviceId(masterUserDevice.getMasterDeviceId());
                return;
            }

            List<UserClock> userClockList = bizClockService.listByMasterDeviceId(masterUserDevice.getDeviceId());
            if (userClockList.size() <= 1) {
                bizClockService.delete(userClockList);
                return;
            }

            //没有说所有，且匹配到多个时间点。提示用户
            String tip = "删除所有闹钟";
            Set<String> set = new HashSet<>();
            for (UserClock userClock : userClockList) {
                if (!set.contains(userClock.getRemark())) {
                    tip = tip + "、或者" + "删除" + userClock.getRemark() + "闹钟";
                    set.add(userClock.getRemark());
                }
            }
            String template = "匹配到多个闹钟" + ",你可以说：" + tip;
            throw CommonException.FAILURE(template);
        }
        String cron = DatetimeSlotUtil.legalCron(datetimeSlot);
        bizClockService.deleteByDeviceIdAndCron(masterUserDevice.getMasterDeviceId(), cron, datetimeSlot.getValue());
    }
}
