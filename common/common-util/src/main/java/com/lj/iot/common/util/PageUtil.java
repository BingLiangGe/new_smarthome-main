package com.lj.iot.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lj.iot.common.base.dto.PageDto;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
public class PageUtil {

    public static <T, P extends PageDto> IPage<T> page(P params) {
        IPage<T> page = new Page<>();
        page.setCurrent(params.getCurrent());
        page.setSize(params.getSize());
        return page;
    }
}
