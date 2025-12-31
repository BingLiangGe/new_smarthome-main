package com.lj.iot.api.demo;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;

public class StudyCodeUtil {

    /**
     * @param dataCode 数据位
     * @param stopTime stop时间
     * @param carrier  高位先发地位先发 00 0f
     * @param head     头载波引导时间
     * @param high     高载波引导时间
     * @return
     */
    public static String buildCode(String dataCode, Double stopTime, String carrier, String head, String high) {
        //data处理格式
        String[] split = dataCode.split(",");
        String length = Integer.toHexString(split.length*8);
        length = length.length()>1?length:"0"+length;
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 4) {
                split[i] = split[i].substring(2);
            }
        }
        dataCode = String.join(",", split);

        int stopTimeInt = (int) (stopTime * 1000);
        stopTimeInt = stopTimeInt > 65520 ? stopTimeInt % 65520 : stopTimeInt;
        String stopTimeStr = decIntHexString(stopTimeInt);
        if (head != null && high != null) {
            head = decIntHexString(Integer.parseInt(head));
            high = decIntHexString(Integer.parseInt(high));
        } else {
            head = "23,a2";
            high = "12,3d ";
        }

        stopTimeStr = AddZero(stopTimeStr);
        head = AddZero(head);
        high = AddZero(high);

        StringBuilder sb = new StringBuilder();
        //红外码拼装流程
        sb.append("20,")   //数据长度
                .append("00,00,00,00,") //未知
                .append(carrier + ",") //高位先发还是低位先发 0f高位先发 00低位先发
                .append("38,") //载波频率 0x36=36K 0x38=38k
                .append("82,67,")
                .append("02,23,")
                .append("82,67,")
                .append("06,9a,")
                .append("c1,") //发送头载波标志
                .append(head + ",") // 发送载波标志
                .append("C2,") //空闲标志
                .append("00,") //0xfff0us时间数量
                .append(high + ",") //空闲时间
                .append("c3,") //发送数据标志
                .append("00,"+length+",") //数据位长度
                .append(dataCode + ",") //数据位
                .append("C2,") //R 复波标志位
                .append("00,")
                .append(stopTimeStr + ",") //stop ms*1000 >fff0(65520) ?  ms*1000%65520：stop ms*1000 求出结果进行10转16
                .append("c1,")
                .append("23,a2,")
                .append("c2,")
                .append("00,")
                .append("12,3d");

        return sb.toString();
    }

    /**
     * @param dataCode 数据位
     * @param stopTime stop时间
     * @param carrier  高位先发地位先发 00 0f
     * @param head     头载波引导时间
     * @param high     高载波引导时间
     * @return
     */
    public static String buildCodeLong(String dataCode, Double stopTime, String carrier, String head, String high) {
        //data处理格式
        String[] split = dataCode.split(",");
        String length = Integer.toHexString(split.length*8);
        length = length.length()>1?length:"0"+length;
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 4) {
                split[i] = split[i].substring(2);
            }
        }
        dataCode = String.join(",", split);

        int stopTimeInt = (int) (stopTime * 1000);
        stopTimeInt = stopTimeInt > 65520 ? stopTimeInt % 65520 : stopTimeInt;
        String stopTimeStr = decIntHexString(stopTimeInt);
        if (head != null && high != null) {
            head = decIntHexString(Integer.parseInt(head));
            high = decIntHexString(Integer.parseInt(high));
        } else {
            head = "23,a2";
            high = "12,3d ";
        }

        stopTimeStr = AddZero(stopTimeStr);
        head = AddZero(head);
        high = AddZero(high);

        StringBuilder sb = new StringBuilder();
        //红外码拼装流程
        sb.append("26,")   //数据长度
                .append("00,00,00,00,") //未知
                .append(carrier + ",") //高位先发还是低位先发 0f高位先发 00低位先发
                .append("38,") //载波频率 0x36=36K 0x38=38k
                .append("82,67,")
                .append("02,23,")
                .append("82,67,")
                .append("06,9a,")
                .append("c1,") //发送头载波标志
                .append(head + ",") // 发送载波标志
                .append("C2,") //空闲标志
                .append("00,") //0xfff0us时间数量
                .append(high + ",") //空闲时间
                .append("c3,") //发送数据标志
                .append("00,"+length+",") //数据位长度
                .append(dataCode + ",") //数据位
                .append("C2,") //R 复波标志位
                .append("00,")
                .append(stopTimeStr + ",") //stop ms*1000 >fff0(65520) ?  ms*1000%65520：stop ms*1000 求出结果进行10转16
                .append("c1,")
                .append("23,a2,")
                .append("c2,")
                .append("00,")
                .append("12,3d,")
                .append("C2,") //R 复波标志位
                .append("00,")
                .append("76,ac,") //stop ms*1000 >fff0(65520) ?  ms*1000%65520：stop ms*1000 求出结果进行10转16
                .append("c1,")
                .append("23,a2,")
                .append("c2,")
                .append("00,")
                .append("12,3d,")
                .append("C2,") //R 复波标志位
                .append("00,")
                .append("76,ac,") //stop ms*1000 >fff0(65520) ?  ms*1000%65520：stop ms*1000 求出结果进行10转16
                .append("c1,")
                .append("23,a2,")
                .append("c2,")
                .append("00,")
                .append("12,3d,")
                .append("C2,") //R 复波标志位
                .append("00,")
                .append("76,ac,") //stop ms*1000 >fff0(65520) ?  ms*1000%65520：stop ms*1000 求出结果进行10转16
                .append("c1,")
                .append("23,a2,")
                .append("c2,")
                .append("00,")
                .append("12,3d");

        return sb.toString();
    }


    /**
     * 3位数补零
     */
    public static String AddZero(String str) {
        switch (str.length()) {
            case 4:
                str = str.substring(0, 2) + "," + str.substring(2);
                break;
            case 3:
                str = "0" + str.substring(0, 1) + "," + str.substring(1);
                break;
            case 2:
                str = "00," + str;
                break;
            case 1:
                str = "00,0" + str;
                break;
            default:
                break;
        }
        return str;
    }

    /**
     * 16进制转10进制
     *
     * @param hex
     * @return
     */
    public static int hexStringDecInt(String hex) {
        int i = Integer.parseInt(hex, 16);
        return i;
    }


    /**
     * 10进制转16进制
     *
     * @param i
     * @return
     */
    public static String decIntHexString(int i) {
        String s = Integer.toHexString(i);
        return s;
    }

    /**
     * 空调键位分析
     */
//    public static String aAirConditioner() {
//        String fileId = "000001";
//        String[] arr = new String[7];
//        for (int j = 0; j < 2; j++) {  //开关
//
//            for (int k = 0; k < 5; k++) { //运转模式
//                for (int l = 0; l < 15; l++) { //温度
//
//                    for (int m = 0; m < 4; m++) { // 风速
//
//                        for (int n = 0; n < 5; n++) { //风向
//
//                            for (int o = 0; o < 5; o++) { //键值
//                                //空调型号 1,开关： 0=开，1=关
//                                //2,运转模式： 0=自动 ，1=制冷， 2=除湿， 3=送风， 4=制热
//                                //3,温度： 16-30 度? 0=16 。。。。 14=30
//                                //4,风速： 0=自动，1=风速 1，2=风速 2，3=风速 3
//                                //5,风向： 0=自动，1=风向 1，2=风向 2，3=风向 3，4=风向 4
//                                //6,键值： 0=开关，1=运转模式，2=温度，3=风 量，4=风向
//                                String keyId = j + "-" + k + "-" + l + "-" + m + "-" + n;
//                                switch (j) {
//                                    case 0:
//                                        arr[2] = "0F";
//                                        break;
//                                    case 1:
//                                        arr[2] = "C3";
//                                }
//                                switch (k) {
//                                    case 0:
//                                        arr[0] = "4E";
//                                        arr[1] = "B1";
//                                        arr[2] = "87";
//                                        break;
//                                    case 1:
//                                        arr[0] = "0E";
//                                        arr[1] = "F1";
//                                        arr[2] = "4B";
//                                        break;
//                                    case 2:
//                                        arr[0] = "0E";
//                                        arr[1] = "F1";
//                                        arr[2] = "87";
//                                        break;
//                                    case 3:
//                                        arr[0] = "8E";
//                                        arr[1] = "71";
//                                        arr[2] = "4B";
//                                        break;
//                                    case 4:
//                                        arr[0] = "0E";
//                                        arr[1] = "F1";
//                                        arr[2] = "C3";
//                                        break;
//                                }
//
//                                //温度计算公式
//                                arr[5] = Integer.toHexString(15+l*15);
//                                //风速公式计算 15+l*15再转16位
//                                arr[3] = Integer.toHexString(15 + m * 15);
//                                //风向无变化
//                                arr[4] = "0F";
//                                arr[6] = "0F";
//                                String irdata = String.join(",", arr);
//                                irdata = buildCode("0x8E,0x71,0x4B,0x1E,0x0F,0x96,0x0F", 40.4, "0f", "3200", "3000");
//                                irdata = irdata.endsWith(",") ? irdata.substring(0, irdata.length() - 1) : irdata;
//                                IrData irData = new IrData();
//                                irData.setIrData(irdata);
//                                irData.setFileId(fileId);
//                                irData.setDataIndex(keyId);
//
//                                irDataRepository.insert(irData);
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    public static void main(String[] args) {
//        System.out.println(buildCode("0x00,0x0c,0x40,0xbf", 47.0,"0f");
//        System.out.println(s);

        System.out.println(buildCode("0x00,0xFF,0x06,0xF9", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0xF0,0xF5", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x09,0xF6", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x12,0xED", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x11,0xEE", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x22,0xDD", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x21,0xDE", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x42,0xBD", 47.0, "0f", "9000", "4500"));
        System.out.println(buildCode("0x00,0xFF,0x41,0xBE", 47.0, "0f", "9000", "4500"));

    }
}
