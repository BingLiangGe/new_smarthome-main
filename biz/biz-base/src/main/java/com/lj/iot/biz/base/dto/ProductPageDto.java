package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageDto extends PageDto {

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 信号类型  "IR":"红外","RF":"射频","INVENTED":"虚设备","MESH":"蓝牙","MASTER":"主控";
     */
    private String signalType;
}
