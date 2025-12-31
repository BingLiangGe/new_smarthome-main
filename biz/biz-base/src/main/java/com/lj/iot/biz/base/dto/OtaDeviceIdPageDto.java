package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtaDeviceIdPageDto extends PageDto {

    /**
     * 产品ID
     */
    public String productId;

    /**
     * 设备ID
     */
    public ArrayList<String> deviceId;

    /**
     * 酒店设备
     */
    private List<String> hotelDevice;
}
