package com.lj.iot.biz.base.dto;


import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 测试红外码请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendIrDataDto {
    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 模型数据
     */
    @NotNull(message = "模型数据不能为空")
    private ThingModel thingModel;

//    //空调电源  0=开，1=关
//    private int onOff = 0;
//    //2,运转模式： 0=自动 ，1=制冷， 2=除湿， 3=送风， 4=制热
//    private int mode = 1;
//    //3,温度： 16-30 度? 0=16 。。。。 14=30
//    private int temp = 0;
//    //4,风速： 0=自动，1=风速 1，2=风速 2，3=风速 3
//    private int fanSpeed = 0;
//    //5,风向： 0=自动，1=风向 1，2=风向 2，3=风向 3，4=风向 4
//    private int airDirection = 0;
    //6,键值：
    // 空调为：0=开关，1=运转模式，2=温度，3=风 量，4=风向
    //非空调按键具体的keyIndex值详见lj_ir_model_key表
    /**
     * 按键key
     */
    @NotNull(message = "必填字段按键key")
    private Integer keyIndex;
}
