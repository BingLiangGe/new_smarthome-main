package com.lj.iot.api.demo;

public class TestCir {


    public static String[]  ir_mode=new String[]{"2B","04","00","00","24","00","38","82","30","02","30","82","30","06","90","C1","23","28","C2","00","11","94","C3","00","20","80","7F","01","FE","C2","00","A2","44","C1","23","28","C2","00","08","CA","C1","02","30","00"};
    //public static String[]  ir_mode=new String[]{"47","00","00","00","00","00","38","82","74","02","46","82","74","06","96","C1","23","34","C2","00","11","B0","C3","00","23","00","07","20","50","02","C2","00","4E","52","C3","00","20","00","20","00","30","C2","00","9C","C4","C1","23","28","C2","00","11","B4","C3","00","23","00","07","20","70","02","C2","00","4E","3E","C3","00","20","00","00","00","10","00"};
    //public static String[]  ir_mode=new String[]{"63","2D","32","C0","01","7D","53","5D","E9","34","65","F8","B7","0F","DE","60","3D","0E","D4","5D","BE","E1","CE","14","83","92","56","D1","EE","00","57","6C","B6","AB","1F","13","52","A8","86","B4","CF","5C","97","51","1A","B9","E6","EF","5E","11","24","F7","CD","93","D1","30","B1","D5","8E","17","0C","E8","23","E8","C5","6A","75","E7","0A","2B","79","54"};
    //public static String[]  ir_mode=new String[]{"47","00","00","00","00","00","38","82","74","02","46","82","74","06","96","C1","23","34","C2","00","11","B0","C3","00","23","04","00","20","50","02","C2","00","4E","52","C3","00","20","00","20","00","00","C2","00","9C","C4","C1","23","28","C2","00","11","B4","C3","00","23","04","00","20","70","02","C2","00","4E","3E","C3","00","20","00","00","00","E0","00"};
    public static void Ir_SpaceSend(String hval, String lval) {
        Integer time = (Integer.parseInt(hval, 16) << 8) + Integer.parseInt(lval, 16);
        System.out.println(time);
    }

    public static void Ir_SpaceSend(Integer hval, Integer lval) {
        Integer time = (hval << 8) + lval;
        System.out.println(time);
    }

    public static void Ir_CarrierSend(String hval, String lval) {
        Integer time = (Integer.parseInt(hval, 16) << 8) + Integer.parseInt(lval, 16);
        System.out.println(time);
    }

    public static void Ir_CarrierSend(Integer hval, Integer lval) {
        Integer time = (hval << 8) + lval;
        System.out.println(time);
    }

    public static void main(String[] args) {
        //Ir_SpaceSend("23", "34");
        System.out.print("length:"+ir_mode.length);
        App_Ir_Data_Send(ir_mode);
    }

    public static Integer[] pulse_generatr_frequency =
            {
                    30000,   // 30khz
                    31000,
                    32000,
                    33000,
                    34000,
                    35000,
                    36000,
                    37000,
                    38000,
                    39000,
                    40000,   // 40khz
                    41000,   // 41khz
                    42000,
                    43000,
                    44000,
                    45000,
                    46000,
                    47000,
                    48000,
                    49000,
                    50000,
                    51000,   // 51khz
                    52000,
                    53000,
                    54000,
                    55000,
                    56000,
                    57000,
                    58000,
                    59000,
                    60000// 60khz
            };

    public static Integer toInt(String ir) {
        return Integer.parseInt(ir, 16);
    }


    public static Integer[] Math_2N_Table = {0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80};

    public static void App_Ir_Data_Send(String[] ir_module) {
        int cnt = 15;
        int chr = Integer.parseInt(ir_module[15], 16);
        int bytes = 0;
        int bits = 0;
        int Bytet;
        int Bitt;
        int j;
        int i;
        System.out.println((pulse_generatr_frequency[toInt(ir_module[6]) - 0x30]));
        while (chr != 0) {
            switch (chr)//???????
            {
                case 0xC1:// 产生载波 连续发射载波的时间
                    Ir_CarrierSend(ir_module[cnt + 1], ir_module[cnt + 2]);//????第二点  两个16位的载波时间  一进去就启动PWM	等待时间结束关闭置零
                    cnt += 3;
                    break;
                case 0xC2: //  空闲时间    第三点  发送空闲时间，不需要载波  时间 一进去就关闭PWM  一直拉低  再释放
                {
                    for (j = 0; j < toInt(ir_module[cnt + 1]); j++) {
                        Ir_SpaceSend("FF", "F0");    //预留15ms
                    }
                    Ir_SpaceSend(ir_module[cnt + 2], ir_module[cnt + 3]);
                    cnt += 4;
                }
                break;
                case 0xC3://	发射二进制数据流
                {
                    bits = (toInt(ir_module[cnt + 1]) << 8) + toInt(ir_module[cnt + 2]);
                    for (i = 0; i < bits; i++) {
                        if ((toInt(ir_module[5]) & 0xf) == 0x00) //?4?=1??MSB,=0 ??LSB
                        {
                            Bytet = i / 8;
                            Bitt = i % 8;
                        } else {
                            Bytet = i / 8;
                            Bitt = (~(i % 8)) & 0x7;
                        }

                        Bitt = Math_2N_Table[Bitt];

                        if ((toInt(ir_module[cnt + 3 + Bytet]) & Bitt) == 0) {
                            if ((toInt(ir_module[7]) & 0x80) == 0x80) {
                                Ir_CarrierSend(toInt(ir_module[7]) & 0x7F, toInt(ir_module[8]));
                                Ir_SpaceSend(toInt(ir_module[9]) & 0x7F, toInt(ir_module[10]));
                            } else {
                                Ir_SpaceSend(toInt(ir_module[7]) & 0x7F, toInt(ir_module[8]));
                                Ir_CarrierSend(toInt(ir_module[9]) & 0x7F, toInt(ir_module[10]));
                            }
                        } else {
                            if ((toInt(ir_module[7]) & 0x80) == 0x80) {
                                Ir_CarrierSend(toInt(ir_module[11]) & 0x7F, toInt(ir_module[12]));
                                Ir_SpaceSend(toInt(ir_module[13]) & 0x7F, toInt(ir_module[14]));
                            } else {
                                Ir_SpaceSend(toInt(ir_module[11]) & 0x7F, toInt(ir_module[12]));
                                Ir_CarrierSend(toInt(ir_module[13]) & 0x7F, toInt(ir_module[14]));
                            }
                        }
                    }
                    if ((toInt(ir_module[5]) & 0xF0) == 0)//PPM?????????? 表示一帧上世纪 发射完
                    {
                        Ir_CarrierSend(toInt(ir_module[7]) & 0x7F, toInt(ir_module[8]));
                    }

                    bytes = bits / 8;
                    if (bits % 8 != 0)
                        bytes = bytes + 1;
                    cnt = (cnt + bytes + 3);

                }
                break;
                default:
            }
            chr = toInt(ir_module[cnt]);
        }
    }
}
