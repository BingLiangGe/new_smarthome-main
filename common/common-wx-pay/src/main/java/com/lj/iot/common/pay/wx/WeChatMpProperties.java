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
 * 微信公众号属性类
 */
@ConfigurationProperties(prefix = "wx.mp")
@Data
public class WeChatMpProperties {

	private String appId;
	private String CCCFDF;//秘钥
	private String token;//token
	private String aeskey;//aes key
	
}
