package com.lj.iot.watchnetty;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class WatchNettyApplicationTests {



    public static void main(String[] args) {

        String msg="e5eee5abbbcc68a23035a420546f48d7DW*358800006310602*018D*UD,281123,090235,V,22.606750,N,113.8399200,E,0.00,0.0,0.0,0,74,81,408,0,00000000,2,1,460,11,26112,107052342,138,26112,107021367,124,10,w14,56:16:51:5c:d8:e7,-52,w16,54:16:51:bc:d8:e7,-52,w17,56:16:51:2c:d8:e7,-52,w18,ee:b9:70:2d:3f:7a,-69,w19,ee:b9:70:7d:3f:7a,-69,w8,54:75:95:17:ce:0a,-76,w2,54:36:9b:15:9e:3e,-77,w3,fc:7c:02:40:3d:db,-82,w1,84:1c:70:77:f8:86,-83,w12,88:2a:5e:f4:76:a8,-83,9999.0";

        String [] datas=msg.split(",");

        for (int i=0;i<datas.length;i++){
            System.out.println("i="+i+",value="+datas[i]);
        }

       /* String msg="[2949c98ba4c8b9aeba706c32c7018050DW*358800001612317*0008*heart,86]";

        Map<String, String> map= msgCaseBloodMany(msg);

        for ( String key: map.keySet()
             ) {
            System.out.println("key="+key+",value="+map.get(key));
        }*/

       /* String msg ="cdd7a29063fcaf9e6ff302e372e1ac04DW*358800002343854*0002*CR";

        System.out.println(msg.split("DW\\*")[1].split("\\*")[0]);*/

       /* String msg = "[3989f761f8fcdb2f258923034667a66fDW*358800001618876*0011*KA,230825,0,0,100]";

        msg = msg.substring(1, msg.length() - 1);

        System.out.print(msg);
        String[] datas = msg.split("DW*")[1].split("\\*");

        Map<String, String> topicMap = new HashMap<>();

        topicMap.put("deviceId", datas[1]);
        topicMap.put("date", datas[1]);
        topicMap.put("number", datas[2]);
        topicMap.put("data", datas[3]);


        for (String key : topicMap.keySet()
        ) {
            System.out.println("key:" + key + ",value:" + topicMap.get(key));
        }*/

        /*String msg = "da2229cf415329ed6433085e331a2b9eDW*358800001618876*00F4*UD2,250823,113309,V,22.606750,N,113.8399200,E,0.00,0.0,0.0,0,100,84,0,0,00000000,1,1,460,1,31042,127346482,148,5,w3,44:56:e2:20:32:a1,-65,w5,1c:40:e8:12:66:8a,-72,w4,0c:84:47:df:94:1d,-78,w1,ec:26:ca:d6:9f:df,-79,w6,90:76:9f:bb:10:ec,-81,9999.0";


        String[] datas = msg.split("DW\\*")[1].split("\\*");
        String deviceId = datas[0];
        String dataArray[] = datas[2].split(",");
        System.out.println(deviceId);
        System.out.println(dataArray);

        double latitude = 0.00;
        double longitude = 0.00;

        for (int i = 0; i < dataArray.length; i++) {

            if ("V".equals(dataArray[i])) {
                latitude = Double.valueOf(dataArray[i + 1]);
            } else if ("N".equals(dataArray[i])) {
                longitude = Double.valueOf(dataArray[i + 1]);
            }

        }

        System.out.println(latitude + "," + longitude);*/


       /* String msg = "[4b902b495ee13371485d52526e1476b8DW*358800001618876*0009*heart,109][a8cf7a5c0124e7f2878b391a7ddccff6DW*358800001618876*000C*blood,123,83][ef1b6e600d2e1a68558da8207463cf0aDW*358800001618876*0009*oxygen,95]";

        String datas[] = msg.split("\\[");

        for (String data : datas
        ) {
            data+="["+data;
        }*/
    }

    private static Map<String, String> msgCaseBloodMany(String msg) {
        String datas[] = msg.split("\\[");

        Map<String, String> dataMap = new HashMap<>();

        for (String data : datas
        ) {

            if ("".equals(data)) {
                continue;
            }

            StringBuffer sb = new StringBuffer();
            sb.append("[").insert(1, data);
            Map<String, String> respMap = msgCase(sb.toString());

            dataMap.put("deviceId", respMap.get("deviceId"));

            if (respMap.get("data").contains("blood")) {
                dataMap.put(respMap.get("data").split(",")[0], respMap.get("data").split(",")[1] + "," + respMap.get("data").split(",")[2]);
            } else {
                dataMap.put(respMap.get("data").split(",")[0], respMap.get("data").split(",")[1]);
            }
        }

        dataMap.put("type", "bloodMany");
        return dataMap;
    }

    private static Map<String, String> msgCase(String msg) {

        // 健康数据组处理
        if (msg.contains("*heart") && msg.contains("*blood") && msg.contains("*oxygen")) {
            return msgCaseBloodMany(msg);
        }

        // 健康数据组_漏数处理
        if (msg.contains("*heart") && msg.contains("*blood")){
            return msgCaseBloodMany(msg);
        }

        msg = msg.substring(1, msg.length() - 1);
        if (msg.contains("*TKQ") || msg.contains("*JMEMBERS") || msg.contains("*APPDOWNLOADURLREQ")) {
            return null;
        }

        // cdd7a29063fcaf9e6ff302e372e1ac04DW*358800002343854*0002*CR


        // 特殊处理
        if (msg.contains("*UD")) {

            String[] datas = msg.split("DW\\*")[1].split("\\*");
            String deviceId = datas[0];
            String dataArray[] = datas[2].split(",");

            String latitude = null;
            String longitude = null;

            for (int i = 0; i < dataArray.length; i++) {
                if ("V".equals(dataArray[i])) {
                    latitude = dataArray[i + 1];
                } else if ("A".equals(dataArray[i])) {
                    latitude = dataArray[i + 1];
                } else if ("N".equals(dataArray[i])) {
                    longitude = dataArray[i + 1];
                }
            }
            Map<String, String> topicMap = new HashMap<>();


            topicMap.put("type", "UD");
            topicMap.put("deviceId", deviceId);
            topicMap.put("latitude", latitude);
            topicMap.put("longitude", longitude);

            return topicMap;
        }

        String[] datas = msg.split("DW*")[1].split("\\*");

        Map<String, String> topicMap = new HashMap<>();

        // 特殊处理
        if (msg.contains("*ICCID")) {
            topicMap.put("type", "ICCID");
            topicMap.put("deviceId", datas[1]);

            return topicMap;
        }

        topicMap.put("deviceId", datas[1]);
        topicMap.put("date", datas[3].split(",")[1]);
        topicMap.put("number", datas[2]);
        topicMap.put("data", datas[3]);
        topicMap.put("type", datas[3].split(",")[0]);
        return topicMap;
    }

}
