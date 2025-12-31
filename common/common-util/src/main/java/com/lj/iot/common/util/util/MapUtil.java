package com.lj.iot.common.util.util;

import java.math.BigDecimal;

public class MapUtil {


    private static double EARTH_RADIUS = 6378137d;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static void main(String[] args) {
        /*System.out.println(isInRange(232,
                new BigDecimal(22.605153), new BigDecimal(113.841207),
                new BigDecimal(22.60706), new BigDecimal(113.840263)));*/
    }


    /*****
     *    判断是否在范围内
     * @param raduis    圆的半径
     * @param lat       点的纬度
     * @param lng       点的经度
     * @param lat1      圆的纬度
     * @param lng1      圆的经度
     * @return
     */
    public static boolean isInRange(int raduis, BigDecimal lat, BigDecimal lng, BigDecimal lat1, BigDecimal lng1) {
        double R = 6378137.0;
        double dLat = (lat1.doubleValue() - lat.doubleValue()) * Math.PI / 180;
        double dLng = (lng1.doubleValue() - lng.doubleValue()) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat.doubleValue() * Math.PI / 180) * Math.cos(lat1.doubleValue() * Math.PI / 180) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        double dis = Math.round(d);
        if (dis <= raduis) {  //点在圆内
            return true;
        } else {
            return false;
        }
    }
}
