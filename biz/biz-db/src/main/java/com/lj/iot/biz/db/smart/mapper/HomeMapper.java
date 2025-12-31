package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 空间,家,房子表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface HomeMapper extends BaseMapper<Home> {
    /**
     * 查询家列表数据
     * @param userId 用户ID
     * @return HomeDataVo
     */
    HomeDataVo queryHomeDataList(String userId);

    IPage<HomePageVo> customPage(IPage<HomePageVo> page,  @Param("params") HomeRoomPageDto pageDto);
}
