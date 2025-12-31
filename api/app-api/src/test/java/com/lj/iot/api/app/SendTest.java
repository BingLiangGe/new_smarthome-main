package com.lj.iot.api.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
public class SendTest {

    @Resource
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Test
    public void send6012Trigger() throws InterruptedException {

        while (true){
            String topic="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"zero\" : 11000,\n" +
                    "      \"keyCode\" : \"open\",\n" +
                    "      \"encodeType\" : \"E201\",\n" +
                    "      \"sym\" : 350,\n" +
                    "      \"syncHead\" : \"[4750, -1437]\",\n" +
                    "      \"controlDeviceId\" : \"\",\n" +
                    "      \"type\" : \"curtain\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"RF\",\n" +
                    "    \"signal\" : [ \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"fe2bedc5653a42b29264df77dfe42695\",\n" +
                    "  \"time\" : 1714011492944\n" +
                    "}");
            MQTT.publish(topic, paramJson.toJSONString());


            String topic2="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson2=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"zero\" : 1600,\n" +
                    "      \"keyCode\" : \"open\",\n" +
                    "      \"encodeType\" : \"E234\",\n" +
                    "      \"sym\" : 350,\n" +
                    "      \"syncHead\" : \"8800\",\n" +
                    "      \"controlDeviceId\" : \"\",\n" +
                    "      \"type\" : \"curtain\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"RF\",\n" +
                    "    \"signal\" : [ \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\", \"0\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"7f9915015b4841f9be1d6c97f5b5fa18\",\n" +
                    "  \"time\" : 1714011492944\n" +
                    "}");
            MQTT.publish(topic2, paramJson2.toJSONString());

            String topic3="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson3=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"operateType\" : \"open\",\n" +
                    "      \"typeId\" : \"2000000300\",\n" +
                    "      \"topProductType\" : \"tv\",\n" +
                    "      \"deviceId\" : \"798494178148397056\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"IR\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"signal\" : [ \"B3\", \"00\", \"00\", \"00\", \"00\", \"00\", \"38\", \"80\", \"00\", \"00\", \"00\", \"80\", \"00\", \"00\", \"00\", \"C1\", \"03\", \"E8\", \"C2\", \"00\", \"02\", \"4E\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"29\", \"CC\", \"C1\", \"03\", \"E8\", \"C2\", \"00\", \"02\", \"4E\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"02\", \"44\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"C2\", \"00\", \"05\", \"AB\", \"C1\", \"02\", \"4E\", \"00\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"fba62280ab984a378adf75067051ff2f\",\n" +
                    "  \"time\" : 1714011492946\n" +
                    "}");
            MQTT.publish(topic3, paramJson3.toJSONString());

            String topic4="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson4=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"fanspeed\" : \"0\",\n" +
                    "      \"temperature\" : \"23\",\n" +
                    "      \"operateType\" : \"open\",\n" +
                    "      \"typeId\" : \"2000000100\",\n" +
                    "      \"workmode\" : \"1\",\n" +
                    "      \"topProductType\" : \"airControl\",\n" +
                    "      \"deviceId\" : \"797424078532161536\",\n" +
                    "      \"powerstate\" : \"1\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"IR\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"signal\" : [ \"26\", \"00\", \"00\", \"00\", \"00\", \"00\", \"38\", \"82\", \"58\", \"02\", \"D0\", \"82\", \"58\", \"07\", \"A8\", \"C1\", \"22\", \"10\", \"C2\", \"00\", \"11\", \"48\", \"C3\", \"00\", \"68\", \"C3\", \"7F\", \"00\", \"00\", \"A0\", \"00\", \"20\", \"00\", \"00\", \"20\", \"00\", \"05\", \"27\", \"00\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"70e06f397568419a94fcf48c3b04277b\",\n" +
                    "  \"time\" : 1714011493440\n" +
                    "}");
            MQTT.publish(topic4, paramJson4.toJSONString());

            String topic5="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson5=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"fanspeed\" : \"0\",\n" +
                    "      \"temperature\" : \"23\",\n" +
                    "      \"operateType\" : \"open\",\n" +
                    "      \"typeId\" : \"2000000100\",\n" +
                    "      \"workmode\" : \"1\",\n" +
                    "      \"topProductType\" : \"airControl\",\n" +
                    "      \"deviceId\" : \"797424149860495360\",\n" +
                    "      \"powerstate\" : \"1\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"IR\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"signal\" : [ \"47\", \"00\", \"00\", \"00\", \"00\", \"00\", \"38\", \"82\", \"74\", \"02\", \"46\", \"82\", \"74\", \"06\", \"96\", \"C1\", \"23\", \"34\", \"C2\", \"00\", \"11\", \"B0\", \"C3\", \"00\", \"23\", \"09\", \"07\", \"20\", \"50\", \"02\", \"C2\", \"00\", \"4E\", \"52\", \"C3\", \"00\", \"20\", \"00\", \"20\", \"00\", \"C0\", \"C2\", \"00\", \"9C\", \"C4\", \"C1\", \"23\", \"28\", \"C2\", \"00\", \"11\", \"B4\", \"C3\", \"00\", \"23\", \"09\", \"07\", \"20\", \"70\", \"02\", \"C2\", \"00\", \"4E\", \"3E\", \"C3\", \"00\", \"20\", \"00\", \"00\", \"00\", \"A0\", \"00\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"382cff904fe34a5e92f0aa5233f5e9ff\",\n" +
                    "  \"time\" : 1714011493440\n" +
                    "}");
            MQTT.publish(topic5, paramJson5.toJSONString());

            String topic6="sys/213350486/16d3a7d87100/thing/service/signal/set";
            JSONObject paramJson6=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"extendData\" : {\n" +
                    "      \"fanspeed\" : \"0\",\n" +
                    "      \"temperature\" : \"23\",\n" +
                    "      \"operateType\" : \"open\",\n" +
                    "      \"typeId\" : \"2000000100\",\n" +
                    "      \"workmode\" : \"1\",\n" +
                    "      \"topProductType\" : \"airControl\",\n" +
                    "      \"deviceId\" : \"835242650775891968\",\n" +
                    "      \"powerstate\" : \"1\"\n" +
                    "    },\n" +
                    "    \"signalType\" : \"IR\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"signal\" : [ \"47\", \"00\", \"00\", \"00\", \"00\", \"00\", \"38\", \"82\", \"74\", \"02\", \"46\", \"82\", \"74\", \"06\", \"96\", \"C1\", \"23\", \"34\", \"C2\", \"00\", \"11\", \"B0\", \"C3\", \"00\", \"23\", \"09\", \"07\", \"20\", \"50\", \"02\", \"C2\", \"00\", \"4E\", \"52\", \"C3\", \"00\", \"20\", \"00\", \"20\", \"00\", \"C0\", \"C2\", \"00\", \"9C\", \"C4\", \"C1\", \"23\", \"28\", \"C2\", \"00\", \"11\", \"B4\", \"C3\", \"00\", \"23\", \"09\", \"07\", \"20\", \"70\", \"02\", \"C2\", \"00\", \"4E\", \"3E\", \"C3\", \"00\", \"20\", \"00\", \"00\", \"00\", \"A0\", \"00\" ]\n" +
                    "  },\n" +
                    "  \"id\" : \"ea1a887074b54af59c4ecd4f550cc6fd\",\n" +
                    "  \"time\" : 1714011493440\n" +
                    "}");
            MQTT.publish(topic6, paramJson6.toJSONString());

            String topic7="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson7=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"11334373\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"1399259fe202\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"brightness\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"colorTemperature\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"260993d5b8e140f883a2c3737715bd22\",\n" +
                    "  \"time\" : 1714011493064\n" +
                    "}");
            MQTT.publish(topic7, paramJson7.toJSONString());


            String topic8="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson8=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"11334373\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"1399259fe203\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"brightness\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"colorTemperature\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"6b94e408d8ce49e2af1f1f0ece46f368\",\n" +
                    "  \"time\" : 1714011493064\n" +
                    "}");
            MQTT.publish(topic8, paramJson8.toJSONString());

            String topic9="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson9=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"1560f369b0b0\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"83d7b58b0bc94d22bed4fe4d5de3d3ff\",\n" +
                    "  \"time\" : 1714011493076\n" +
                    "}");
            MQTT.publish(topic9, paramJson9.toJSONString());

            String topic10="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson10=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"11334373\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"1399259fe204\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"brightness\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"colorTemperature\",\n" +
                    "      \"value\" : \"0\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"d49de7f8b5774103a489ce68134ded08\",\n" +
                    "  \"time\" : 1714011493291\n" +
                    "}");
            MQTT.publish(topic10, paramJson10.toJSONString());


            String topic11="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson11=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7096\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"d8869bb540a54c7ca8247b9eef2b1948\",\n" +
                    "  \"time\" : 1714011493300\n" +
                    "}");
            MQTT.publish(topic11, paramJson11.toJSONString());

            String topic12="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson12=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7098\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"6e352fb3ef7e44e9aa46c2b5ffe093e4\",\n" +
                    "  \"time\" : 1714011493300\n" +
                    "}");
            MQTT.publish(topic12, paramJson12.toJSONString());

            String topic13="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson13=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"213350486\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"15da618c9a97\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate_2\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"16c37ea944ed428293c8df4a2c6dfbec\",\n" +
                    "  \"time\" : 1714011493304\n" +
                    "}");
            MQTT.publish(topic13, paramJson13.toJSONString());


            String topic14="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson14=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7100\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"d1902886c4134191895ffdf24a8bea22\",\n" +
                    "  \"time\" : 1714011493305\n" +
                    "}");
            MQTT.publish(topic14, paramJson14.toJSONString());

            String topic15="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson15=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7086\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"e99ee4c4ede54aebb6df0e769c497742\",\n" +
                    "  \"time\" : 1714011493312\n" +
                    "}");
            MQTT.publish(topic15, paramJson15.toJSONString());

            String topic16="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson16=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"17002943\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"14155bde8122\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate_1\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"powerstate_2\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"powerstate_3\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"992452c2c27646078776a740bca61362\",\n" +
                    "  \"time\" : 1714011493314\n" +
                    "}");
            MQTT.publish(topic16, paramJson16.toJSONString());


            String topic17="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson17=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"17002943\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"12e25b0c6382\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate_1\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"powerstate_2\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    }, {\n" +
                    "      \"identifier\" : \"powerstate_3\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"2c2749a5a0e5419babe23c848602baac\",\n" +
                    "  \"time\" : 1714011493316\n" +
                    "}");
            MQTT.publish(topic17, paramJson17.toJSONString());

            String topic18="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson18=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7095\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"814e759ee1bc46edb46df3101c0bc96d\",\n" +
                    "  \"time\" : 1714011493322\n" +
                    "}");
            MQTT.publish(topic18, paramJson18.toJSONString());

            String topic19="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson19=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7103\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"1f2ba795bdee464c8bfd7797c2f0613d\",\n" +
                    "  \"time\" : 1714011493324\n" +
                    "}");
            MQTT.publish(topic19, paramJson19.toJSONString());


            String topic20="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson20=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"213350486\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"15da618c9a97\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate_1\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"573ee9c6172b4e9381e594fcfaa04fb6\",\n" +
                    "  \"time\" : 1714011493327\n" +
                    "}");
            MQTT.publish(topic20, paramJson19.toJSONString());


            String topic21="sys/213350486/16d3a7d87100/thing/service/property/set";
            JSONObject paramJson21=JSONObject.parseObject("{\n" +
                    "  \"data\" : {\n" +
                    "    \"productId\" : \"10623229\",\n" +
                    "    \"isTrigger\" : 1,\n" +
                    "    \"deviceId\" : \"161f5b6d7102\",\n" +
                    "    \"properties\" : [ {\n" +
                    "      \"identifier\" : \"powerstate\",\n" +
                    "      \"value\" : \"1\"\n" +
                    "    } ]\n" +
                    "  },\n" +
                    "  \"id\" : \"d2916f92ef804a429f2fa18a52655664\",\n" +
                    "  \"time\" : 1714011493343\n" +
                    "}");
            MQTT.publish(topic21, paramJson21.toJSONString());
            Thread.sleep(10 * 1000);
        }

    }


    @Test
    public  void sendRfCode() throws InterruptedException {

        String topic="sys/213350486/16d3a7d87100/thing/service/signal/set";

        while (true){

            for (int i=0;i<20;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject paramJson=JSONObject.parseObject("{\n" +
                                "  \"data\" : {\n" +
                                "    \"extendData\" : {\n" +
                                "      \"zero\" : 600,\n" +
                                "      \"keyCode\" : \"close\",\n" +
                                "      \"encodeType\" : \"E201\",\n" +
                                "      \"sym\" : 200,\n" +
                                "      \"syncHead\" : \"[5000, -600]\",\n" +
                                "      \"controlDeviceId\" : \"\",\n" +
                                "      \"type\" : \"racks\"\n" +
                                "    },\n" +
                                "    \"signalType\" : \"RF\",\n" +
                                "    \"signal\" : [ \"73\", \"2\", \"6\", \"14\", \"6c\", \"2\", \"5a\", \"0\", \"c8\", \"3d\", \"cc\", \"8\", \"3a\", \"2\", \"d8\", \"5\", \"1\", \"0\", \"43\", \"23\", \"c0\", \"0\" ]\n" +
                                "  },\n" +
                                "  \"id\" : \"78663cc6b766470c97e98189dd9eda4d\",\n" +
                                "  \"time\" : 1713750766588\n" +
                                "}");

                        MQTT.publish(topic, paramJson.toJSONString());
                    }
                }).start();
            }

            Thread.sleep(30);
        }

    }


    @Test
    public void sendDataTest() {
/*
        SendDataDto dto = new SendDataDto();

        dto.setDeviceId("123132131231232132");

        UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());

        dto.setThingModel(userDevice.getThingModel());

        bizUserDeviceService.sendData(dto, OperationEnum.APP_C);*/
    }
}
