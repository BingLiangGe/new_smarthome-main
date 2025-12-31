package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HomeUserJoinPageDto;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;
import com.lj.iot.biz.db.smart.entity.HomeUserJoin;

/**
 * <p>
 * 家和用户关联申请表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
public interface IHomeUserJoinService extends IService<HomeUserJoin> {

    /***
     * @param pageDto
     * @return
     */
    IPage<HomeUserJoinVo> customPage(HomeUserJoinPageDto pageDto);
}
