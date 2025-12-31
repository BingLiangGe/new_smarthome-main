package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.MusicProduct;

/**
 *
 * 音乐产品表 服务类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IMusicProductService extends IService<MusicProduct> {

    IPage<MusicProduct> customPage(PageDto pageDto);
}
