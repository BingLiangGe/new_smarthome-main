package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 用户账号表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    IPage<UserAccount> customPage(IPage<UserAccount> page, @Param("params") PageDto paramDto);
}
