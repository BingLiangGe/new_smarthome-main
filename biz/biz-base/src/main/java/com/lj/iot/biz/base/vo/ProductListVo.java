package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private List<ProductListItemVo> data = new ArrayList<>();
}
