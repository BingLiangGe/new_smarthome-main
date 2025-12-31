package com.lj.iot.api.app.web.open;

import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.service.BizHomeRoomService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 主控同步房间相关接口
 *
 * @author wanyuli
 * @since 2023-1-31
 */
@RestController
@RequestMapping("api/open/home_room")
public class OpenHomeRoomController {
    @Autowired
    private BizHomeRoomService bizHomeRoomService;

    /**
     * 查询主控所有家房间列表
     *
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<HomeRoom>> list(@Valid OfflineHomeRoomDto dto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.OfflineList(dto.getDeviceId(),dto.getRoomId()));
    }
}
