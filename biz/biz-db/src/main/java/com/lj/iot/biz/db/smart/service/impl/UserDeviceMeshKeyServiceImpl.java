package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.mapper.UserDeviceMeshKeyMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-01-02
 */
@DS("smart")
@Service
public class UserDeviceMeshKeyServiceImpl extends ServiceImpl<UserDeviceMeshKeyMapper, UserDeviceMeshKey> implements IUserDeviceMeshKeyService {

}
