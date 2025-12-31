package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIRDataDto implements Serializable {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;

    //此参数是否为空调
    private boolean airCondition = false;
    //红外码组id
    private String kfid;
    //空调电源  0=开，1=关
    private String powerstate;
    //2,运转模式： 0=自动 ，1=制冷， 2=除湿， 3=送风， 4=制热
    private String workmode;
    //3,温度： 16-30 度? 0=16 。。。。 14=30
    private String temperature;
    //4,风速： 0=自动，1=风速 1，2=风速 2，3=风速 3
    private String fanspeed;
    //5,风向： 0=自动，1=风向 1，2=风向 2，3=风向 3，4=风向 4
    private String airdirection;
    //6,键值：
    // 空调为：0=开关，1=运转模式，2=温度，3=风 量，4=风向
    //非空调按键具体的keyIndex值详见lj_ir_model_key表
    private Integer keyIndex = 0;
}
