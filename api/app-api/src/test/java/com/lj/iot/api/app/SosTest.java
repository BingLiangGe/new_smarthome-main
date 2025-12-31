package com.lj.iot.api.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.base.dto.FutureDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class SosTest {

    @Autowired
    private VmsProperties vmsProperties;

    @Test
    public void test() {

        JSONObject params = new JSONObject();
        String deviceName = "唐元杰";
        String ttsCode = "TTS_287805065";
        String mobile = "18074667983";
        params.put("deviceName", deviceName);


        Config config = new Config()
                .setEndpoint(vmsProperties.getDomain())
                .setRegionId(vmsProperties.getRegionId())
                .setAccessKeyId(vmsProperties.getAccessKeyId())
                .setAccessKeyCCCFDF(vmsProperties.getAccessCCCFDF());

        final SingleCallByTtsRequest request = new SingleCallByTtsRequest()
                .setTtsCode(ttsCode)
                .setCalledNumber(mobile)
                .setCalledShowNumber(vmsProperties.getCalledShowNumber())
                .setTtsParam(JSON.toJSONString(params));
        try {
            Client client = new Client(config);
            final SingleCallByTtsResponse response = client.singleCallByTts(request);
            log.info("VMS.call: {}", JSON.toJSONString(response));


            /*return FutureDto.builder()
                    .success("OK".equalsIgnoreCase(response.getBody().getCode()))
                    .code(response.getBody().getCode())
                    .message(response.getBody().getMessage())
                    .build();*/
        } catch (Exception e) {
            log.error("CALL.call呼叫失败", e);
           /* return FutureDto.builder()
                    .success(false)
                    .code("-1")
                    .message(e.getMessage())
                    .build();*/
        }
    }
}
