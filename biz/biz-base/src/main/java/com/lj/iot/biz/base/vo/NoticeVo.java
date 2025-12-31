package com.lj.iot.biz.base.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long unHandle;

    private IPage<T> page;
}
