package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserJoinPageDto;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;
import com.lj.iot.biz.db.smart.entity.HomeUserJoin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.Product;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 家和用户关联申请表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
public interface HomeUserJoinMapper extends BaseMapper<HomeUserJoin> {

    IPage<HomeUserJoinVo> customPage(IPage<Product> page, @Param("params") HomeUserJoinPageDto pageDto);

}
