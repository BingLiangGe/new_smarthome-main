package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeInfoVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.db.smart.entity.HomeUser;

import java.util.List;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
public interface BizHomeUserService {

    /**
     * 设置默认家
     *
     * @param idDto
     * @param userId
     */
    HomeUser setDefaultHome(HomeUserIdDto idDto, String userId);

    /**
     * 分页查询
     *
     * @return
     */
    IPage<HomeUser> customPage(HomeUserPageDto pageDto);


    List<HomeDataVo> listHome(String userId);

    List<HomeUserVo> findHomeUserList(Long homeId,String userId);

    /**
     * 家庭信息
     *
     * @param homeId
     * @param userId
     * @return
     */
    HomeInfoVo info(Long homeId, String userId);

    /**
     * 根据homeId查询homeId下的所有用户
     *
     * @param homeId
     * @return
     */
    List<String> getMemberUserIdsByHomeId(Long homeId);

    List<String> getEditMemberUserIdsByHomeId(Long homeId);

    /**
     * 解除家用户关系(管理员用)
     *
     * @param
     * @return
     */
    HomeUser kickOut(HomeUserIdDto idDto, String userId);

    /**
     * 用户退出
     *
     * @param
     */
    HomeUser signOut(HomeUserIdDto idDto, String userId);

    /**
     * 不是成员抛异常
     *
     * @param homeId
     * @param userId
     */
    HomeUser noIsAllThrow(Long homeId, String userId);

    /**
     * 不是管理员抛异常
     *
     * @param homeId
     * @param userId
     */
    void noIsAdminThrow(Long homeId, String userId);

    void noIsEditThrow(Long homeId, String userId);

    String setHomeUserDeviceAuthority(SetHomeUserDeviceAuthorityDto dto,String userId);

    String setManage(HomeUserIdDto dto, String uId);
}
