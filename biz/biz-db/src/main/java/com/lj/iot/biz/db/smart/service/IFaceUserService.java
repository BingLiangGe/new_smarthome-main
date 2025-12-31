package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.FaceUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tyj
 * @since 2023-08-19
 */
public interface IFaceUserService extends IService<FaceUser> {


    List<String> selectAllMobile();
}
