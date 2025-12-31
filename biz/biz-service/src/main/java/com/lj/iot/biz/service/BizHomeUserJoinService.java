package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeUserJoinHandleDto;
import com.lj.iot.biz.base.dto.InviteUserDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
public interface BizHomeUserJoinService {

    /**
     * 邀请数据
     *
     * @param pageDto
     * @return
     */
    IPage<HomeUserJoinVo> pageMyHome(PageDto pageDto, String userId);

    /**
     * 申请数据
     *
     * @param pageDto
     * @return
     */
    IPage<HomeUserJoinVo> pageOtherHome(PageDto pageDto, String userId);

    /**
     * 邀请
     *
     * @param dto
     */
    void invite(InviteUserDto dto,String userId);

    /**
     * 处理邀请
     *
     * @param dto
     */
    Long handleInvite(HomeUserJoinHandleDto dto,String userId);

    /**
     * 申请
     *
     * @param dto
     * @param userId
     */
    void apply(HomeIdDto dto, String userId);

    /**
     * 处理申请
     *
     * @param dto
     */
    void handleApply(HomeUserJoinHandleDto dto,String userId);

}


