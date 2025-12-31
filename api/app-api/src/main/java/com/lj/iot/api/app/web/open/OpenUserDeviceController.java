package com.lj.iot.api.app.web.open;

import com.lj.iot.biz.base.dto.OfflineUserDeviceDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 主控同步设备相关接口
 *
 * @author wanyuli
 * @since 2023-1-31
 */
@RestController
@RequestMapping("api/open/user_device")
public class OpenUserDeviceController {
    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    /**
     * 查询主控所属家下设备数据
     *
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<UserDevice>> list(@Valid OfflineUserDeviceDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.OfflineList(dto.getMasterDeviceId(),dto.getDeviceId()));
    }
}
