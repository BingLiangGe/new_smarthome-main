package com.lj.iot.common.util;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceIdUtils {
    /**
     * 邀请码总共32个码[一般不允许变更，否则导致之前生成的邀请码无法正确反解析]
     */
    private static String CODE = "qef9wphbkrmgdzca635vsxiu2jtnl7y8";
    private static final String[] CODE_ARR = CODE.split("");
    /**
     * 开始时间截 (2018-01-01)
     */
    private static final long end = 1514736000000L;

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 7L;
    /**
     * 生成序列的掩码，这里为4095 (0b1111111=127)
     */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 毫秒内序列(0~127)
     */
    private static long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = -1L;

    public static synchronized Long nextTimestamp() {
        long timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            log.error("IdUtils.nextId timestamp < lastTimestamp:timestamp={},lastTimestamp={}", timestamp, lastTimestamp);
            throw CommonException.INSTANCE(CodeConstant.FAILURE, "系统繁忙 !");
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        return timestamp;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public static synchronized String hexId() {
        // 上次生成ID的时间截
        Long timestamp = nextTimestamp();

        // 移位并通过或运算拼到一起组成64位的ID
        long longId = ((timestamp - end) << sequenceBits) | sequence;
        return Long.toHexString(longId);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }


    public static void main(String[] args) {
        System.out.println(hexId());
    }
}
