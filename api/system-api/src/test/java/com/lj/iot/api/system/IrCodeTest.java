package com.lj.iot.api.system;

import com.lj.iot.common.util.OkHttpUtils;

import java.io.IOException;

public class IrCodeTest {

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 100000; i++) {
            String result = OkHttpUtils.get("http://2k8p65.natappfree.cc/ljwl/keyevent.php?mac=ff92e0f2cd6d30a5&kfid=012030&par=0-0-0-0-0-X");
            System.out.println("size=" + i + ",result=" + result);
        }
    }
}
