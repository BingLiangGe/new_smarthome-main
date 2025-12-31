package com.lj.iot.biz.base.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExcelImportDto {

    @ExcelProperty(value = "deviceId")
    private String deviceId;
}
