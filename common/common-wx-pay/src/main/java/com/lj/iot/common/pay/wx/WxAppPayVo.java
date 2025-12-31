package com.lj.iot.common.pay.wx;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class WxAppPayVo implements Serializable {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;
    private String appid;
    private String partnerid;
    private String prepayid;
    @JSONField(name = "package")
    private String packageStr;
    private String noncestr;
    private String timestamp;
    private String sign;
    private String signType;
    private Long orderId;
}
