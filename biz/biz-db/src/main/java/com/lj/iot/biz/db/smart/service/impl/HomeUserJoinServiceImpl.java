package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserJoinPageDto;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;
import com.lj.iot.biz.db.smart.entity.HomeUserJoin;
import com.lj.iot.biz.db.smart.mapper.HomeUserJoinMapper;
import com.lj.iot.biz.db.smart.service.IHomeUserJoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 家和用户关联申请表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
@DS("smart")
@Service
public class HomeUserJoinServiceImpl extends ServiceImpl<HomeUserJoinMapper, HomeUserJoin> implements IHomeUserJoinService {


    @Override
    public IPage<HomeUserJoinVo> customPage(HomeUserJoinPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto),pageDto);
    }
}
