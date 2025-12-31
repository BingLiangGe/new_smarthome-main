/**  
 * All rights Reserved, Designed By www.5idong.com
 * @Title:  OSSProperties.java   
 * @Package com.ydd.config.alioss   
 * @Description:    描述   
 * @author: DeanLiu     
 * @date:   2020年7月16日 下午5:14:00   
 * @version V1.0 
 * @Copyright: 
 */
package com.lj.iot.common.pay.wx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付属性类
 */
@ConfigurationProperties(prefix = "wx.pay")
@Data
public class WeChatPayProperties {

	private String appId;
	private String mchId;//商户号
	private String apiV3Key;//api V3密钥
	private String keyPath;//私钥key地址
	private String certPath;//私钥证书地址
    private String unifiedOrderUrl;//统一下单地址
    private String queryOrderUrl;//微信订单查询地址
	
}
