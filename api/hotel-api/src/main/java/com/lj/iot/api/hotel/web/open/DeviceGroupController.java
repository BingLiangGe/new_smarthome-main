package com.lj.iot.api.hotel.web.open;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.db.smart.entity.DeviceGroup;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceGroupService;
import com.lj.iot.biz.db.smart.service.IHomeRoomService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备组
 *
 * @author hao
 * @Date 2023/2/17
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/device_group")
public class DeviceGroupController {
    @Autowired
    private IDeviceGroupService deviceGroupService;
    @Resource
    IUserDeviceService userDeviceService;
    @Autowired
    private IHomeRoomService homeRoomService;



    /**
     * 根据group查询组
     *
     * @return
     */
    @GetMapping("/find_group")
    public CommonResultVo<List<DeviceGroup>> findGroup(@Valid DeviceIdDto dto) {
        //查询当前网关 如果不是这个网关的不查询
       // String gatway = dto.getDeviceId().substring(dto.getDeviceId().length() - 2);
        //String gatway = dto.getDeviceId().substring(0,dto.getDeviceId().length()-2);

        //查询主控下的组数据
        //查询当前设备
        UserDevice byId2 = userDeviceService.getById(dto.getDeviceId());

        List<UserDevice> list  = userDeviceService.findGroupId(byId2.getMasterDeviceId());
        List<DeviceGroup> lists = new ArrayList<>();

        // List<DeviceGroup> list = deviceGroupService.list(new QueryWrapper<>(build).likeRight("device_id",gatway).groupBy("group_id"));
        for (int i = 0; i < list.size(); i++) {
            String groupId1 = list.get(i).getGroupId();
            DeviceGroup deviceGroup = new DeviceGroup();
            List<DeviceGroup> list1 = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(groupId1).build()));

            if (list1.isEmpty()){
                log.info("DeviceGroupList数据不存在,deviceId={}",groupId1);
                continue;
            }
            ValidUtils.listIsEmptyThrow(list1, "DeviceGroupList数据不存在");
            deviceGroup.setGroupName(list1.get(0).getGroupName());
            deviceGroup.setGroupId(groupId1);
            deviceGroup.setId(list1.get(0).getId());
            deviceGroup.setDeviceId(dto.getDeviceId());
            lists.add(deviceGroup);
            for (int j = 0; j < list1.size(); j++) {
                String deviceId = list1.get(j).getDeviceId();
                if(!deviceId.equals("")){
                    UserDevice byId = userDeviceService.getById(deviceId);
                    String deviceName = byId.getCustomName();
                    String imagesUrl = byId.getImagesUrl();
                    list1.get(j).setImagesUrl(imagesUrl);
                    Long roomId = byId.getRoomId();
                    list1.get(j).setDeviceName(deviceName);
                    HomeRoom byId1 = homeRoomService.getById(roomId);
                    list1.get(j).setRoomName(byId1.getRoomName());
                }

            }
            lists.get(i).setList(list1);
        }
        return CommonResultVo.SUCCESS(lists);
    }


    /**
     * 新增组
     *
     * @return
     */
    @PostMapping("/add_group")
    public CommonResultVo<Boolean> addGroup() {
        String uId = UserDto.getUser().getUId();
        String groupId = IdUtils.sId();
        //根据userid 查询有几个组
        List<DeviceGroup> list = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().userId(uId).build()));
        DeviceGroup build = DeviceGroup.builder().groupId(groupId).userId(uId).groupName("分组"+(list.size()+1)).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
        return CommonResultVo.SUCCESS(deviceGroupService.save(build));
    }




}
