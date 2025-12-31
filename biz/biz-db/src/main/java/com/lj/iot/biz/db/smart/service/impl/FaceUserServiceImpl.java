package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.FaceUser;
import com.lj.iot.biz.db.smart.mapper.FaceUserMapper;
import com.lj.iot.biz.db.smart.service.IFaceUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-08-19
 */
@DS("smart")
@Service
public class FaceUserServiceImpl extends ServiceImpl<FaceUserMapper, FaceUser> implements IFaceUserService {

    @Resource
    private FaceUserMapper faceUserMapper;

    @Override
    public List<String> selectAllMobile() {
        return faceUserMapper.selectAllMobile();
    }
}
