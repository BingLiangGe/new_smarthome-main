package com.lj.iot.biz.db.smart.util;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface TestLibrary extends Library {
    //c库文件需要部署在服务器系统文件夹 /usr/lib/下
    TestLibrary INSTANCE = (TestLibrary) Native.loadLibrary(("/usr/lib/mylib.so"),TestLibrary.class);

    //声明要调用的so中的方法
    void convRemotcode(byte[] srcData,byte[] dstData,Integer DataLen);

    char hello(char s);

}
