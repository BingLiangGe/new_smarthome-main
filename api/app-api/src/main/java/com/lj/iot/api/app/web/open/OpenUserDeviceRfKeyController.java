package com.lj.iot.api.app.web.open;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.OfflineUserDeviceRfKeyDto;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.biz.service.BizUserDeviceRfKeyService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 同步射频按键
 */
@RestController
@RequestMapping("/api/open/user_device_rf_key")
public class OpenUserDeviceRfKeyController {

    @Autowired
    private BizUserDeviceRfKeyService bizUserDeviceRfKeyService;

    /**
     * 同步按键数据获取
     *
     * @param dto
     * @return
     */
    @GetMapping("list")
    public CommonResultVo<List<UserDeviceRfKey>> list(@Valid OfflineUserDeviceRfKeyDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceRfKeyService.OfflineList(dto.getMasterDeviceId(),dto.getDeviceId()));
    }
}

