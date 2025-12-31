package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.db.smart.entity.HomeUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 家和用户关联表 Mapper 接口
 *
 * @author xm
 * @since 2022-07-13
 */
public interface HomeUserMapper extends BaseMapper<HomeUser> {

    IPage<HomeUser> customPage(IPage<HomeUser> page, @Param("params") HomeUserPageDto pageDto);

    List<HomeUserVo> findHomeUserList(@Param("homeId") Long homeId, @Param("userId") String userId);

    List<HomeDataVo> listHome(@Param("userId") String userId);

    HomeUser defaultHome(@Param("userId") String userId);

    List<String> getEditMemberUserIdsByHomeId(@Param("homeId")Long homeId);

}
