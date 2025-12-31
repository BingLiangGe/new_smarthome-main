package com.lj.iot.common.pay.wx;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxPayNotifyReq implements Serializable {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String create_time;
    private String event_type;
    private String summary;
    private String resource_type;

    private PayResource resource;

    @Data
    public class PayResource implements Serializable {

        /**
         * @Fields serialVersionUID :
         */
        private static final long serialVersionUID = 1L;
        private String algorithm;
        private String original_type;
        private String ciphertext;
        private String associated_data;
        private String nonce;

    }
}
