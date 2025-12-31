package com.lj.iot.biz.base.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ProductVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;

    private String productName;
}
