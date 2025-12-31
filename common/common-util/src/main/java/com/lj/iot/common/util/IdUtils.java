package com.lj.iot.common.util;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class IdUtils {
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
     * 机器id所占的位数
     */
    private static final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private static final long dataCenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private static final long maxDataCenterId = ~(-1L << dataCenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private static long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private static long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private static long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private static long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param workerId
     *            工作ID (0~31)
     * @param datacenterId
     *            数据中心ID (0~31)
     */

    static {
        Long ip = IPUtils.ipToLong(IPUtils.getLocalIP());
        workerId = ip & maxWorkerId;
        datacenterId = ip >> 8 & maxDataCenterId;
    }


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
    public static synchronized String nextId() {
        // 上次生成ID的时间截
        Long timestamp = nextTimestamp();

        // 移位并通过或运算拼到一起组成64位的ID
        long longId = ((timestamp - end) << timestampLeftShift) | (datacenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + longId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public static synchronized String nextId(String pre) {
        // 上次生成ID的时间截
        Long timestamp = nextTimestamp();

        // 移位并通过或运算拼到一起组成64位的ID
        long longId = ((timestamp - end) << timestampLeftShift) | (datacenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        return pre + DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now()).substring(1) + longId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public static synchronized Long uid() {
        // 上次生成ID的时间截
        Long timestamp = nextTimestamp();

        // 移位并通过或运算拼到一起组成64位的ID
        long longId = ((timestamp - end) << timestampLeftShift) | (datacenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        return longId;
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
        long longId = ((timestamp - end) << timestampLeftShift) | (datacenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        return Long.toHexString(longId);
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public static synchronized String sId() {
        // 上次生成ID的时间截
        Long timestamp = nextTimestamp();

        // 移位并通过或运算拼到一起组成64位的ID
        long longId = ((timestamp - end) << timestampLeftShift) | (datacenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        return longId + "";
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
     * 生成12位不重复码
     *
     * @return
     */
    public static String getUniCode() {

        Long id = IdUtils.uid();
        //加上混淆ID的数据，用以生成邀请码
        long tmp;
        int index;
        String inviteCode = "";
        for (int i = 0; i < 12; i++) {
            tmp = (id) & 31;
            index = (int) tmp;
            inviteCode = CODE_ARR[index] + inviteCode;
            id = id >> 5;
        }
        return inviteCode;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
