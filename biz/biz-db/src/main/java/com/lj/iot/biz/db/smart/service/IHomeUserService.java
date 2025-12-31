package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.db.smart.entity.HomeUser;

import java.util.List;

/**
 * 家和用户关联表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IHomeUserService extends IService<HomeUser> {

    /**
     * 分页查询家数据
     *
     * @param pageDto
     * @return
     */
    IPage<HomeUser> customPage(HomeUserPageDto pageDto);

    List<String> getEditMemberUserIdsByHomeId(Long homeId);


    /**
     * 查询家成员信息
     *
     * @param homeId 家ID
     * @return
     */
    List<HomeUserVo> findHomeUserList(Long homeId,String userId);

    List<HomeDataVo> listHome(String userId);


    HomeUser defaultHome(String userId);

    /**
     * 查询成员信息通过家ID
     *
     * @param homeId
     * @param userId
     * @return
     */
    HomeUser findByHomeIdAndUserId(Long homeId, String userId);

    Long getHomeIdById(Long homeUserId);

    void deleteAndCache(HomeUser homeUser);

    HomeUser addAndCache(HomeUser homeUser);

    HomeUser editAndCache(HomeUser homeUser, String type);

    HomeUser getOneCache(Long homeId, String memberUserId);
}
