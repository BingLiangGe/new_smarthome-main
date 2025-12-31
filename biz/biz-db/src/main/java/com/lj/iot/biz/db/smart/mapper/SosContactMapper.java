package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.dto.IdPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;
import org.apache.ibatis.annotations.Param;

/**
 * 紧急呼叫联系人 Mapper 接口
 *
 * @author xm
 * @since 2022-07-13
 */
public interface SosContactMapper extends BaseMapper<SosContact> {

    IPage<SosContact> customPage(IPage<SosContact> page, @Param("params") HomeUserPageDto pageDto);
}
