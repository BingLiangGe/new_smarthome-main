package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lj.iot.biz.base.dto.HomeDirectDeviceDto;
import com.lj.iot.biz.db.smart.entity.HomeDirectDevice;
import com.lj.iot.biz.db.smart.mapper.HomeDirectDeviceMapper;
import com.lj.iot.biz.db.smart.service.IHomeDirectDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  直连设备服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-30
 */
@DS("smart")
@Service
public class HomeDirectDeviceServiceImpl extends ServiceImpl<HomeDirectDeviceMapper, HomeDirectDevice> implements IHomeDirectDeviceService {

    @Override
    public HomeDirectDevice add(HomeDirectDeviceDto dto, String uId) {
        HomeDirectDevice dbHomeDirectDevice = this.getOne(new LambdaQueryWrapper<HomeDirectDevice>()
            .eq(HomeDirectDevice::getHomeId, dto.getHomeId())
        );
        HomeDirectDevice homeDirectDevice = null;
        if(dbHomeDirectDevice == null){
            homeDirectDevice=HomeDirectDevice.builder().data(dto.getData())
                    .homeId(dto.getHomeId())
                    .build();
            this.save(homeDirectDevice);
        }else{
            homeDirectDevice=HomeDirectDevice.builder().data(dto.getData())
                    .homeId(dto.getHomeId()).id(dbHomeDirectDevice.getId())
                    .build();
            this.updateById(homeDirectDevice);
        }
        return homeDirectDevice;
    }

    @Override
    public HomeDirectDevice edit(HomeDirectDeviceDto dto, String uId) {
        ValidUtils.isTrueThrow(dto.getId()<=0,"主键IDbu");
        HomeDirectDevice homeDirectDevice=HomeDirectDevice.builder().data(dto.getData())
                .homeId(dto.getHomeId())
                .id(dto.getId())
                .build();
        this.updateById(homeDirectDevice);
        return homeDirectDevice;
    }

    @Override
    public HomeDirectDevice get(Long homeId) {
        return this.getOne(new LambdaQueryWrapper<HomeDirectDevice>().eq(HomeDirectDevice::getHomeId, homeId));
    }
}
