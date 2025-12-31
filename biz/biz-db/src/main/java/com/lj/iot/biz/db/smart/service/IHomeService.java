package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.Home;

/**
 *
 * 空间,家,房子表 服务类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IHomeService extends IService<Home> {
    /**
     * 通过用户ID获取所有家列表数据
     * @param uId 用户ID
     * @return
     */
    HomeDataVo queryHomeDataList(String uId);

    IPage<HomePageVo> customPage(HomeRoomPageDto pageDto);

    /**
     *  根据家ID和用户ID获取家信息(权限校验)
     * @param homeId 家ID
     * @param userId 用户ID
     * @return
     */
    Home findHomeByHomeIdAndUserId(Long homeId, String userId);
}
