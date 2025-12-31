package com.lj.iot.common.aiui.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.CronPatternBuilder;
import cn.hutool.cron.pattern.Part;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.CronExpression;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatetimeSlotUtil {

    public static String slot2Cron(IntentDto.Slot datetimeSlot) {
        JSONObject jsonObject = JSON.parseObject(datetimeSlot.getNormValue());
        String datetime = jsonObject.getString("datetime");
        String suggestDatetime = jsonObject.getString("suggestDatetime");

        String cron = "";
        //1、偏移时间
        // datetime为 O+时间段或O-时间段。suggestTime由当前时间加上或减去时间段，计算得到。
        if (datetime.startsWith("O+")) {
            LocalDateTime localDateTime = toLocalDateTime(suggestDatetime);
            cron = CronPatternBuilder
                    .of()
                    .set(Part.SECOND, localDateTime.getSecond() + "")
                    .set(Part.MINUTE, localDateTime.getMinute() + "")
                    .set(Part.HOUR, localDateTime.getHour() + "")
                    .set(Part.DAY_OF_MONTH, localDateTime.getDayOfMonth() + "")
                    .set(Part.DAY_OF_WEEK, "?")
                    .set(Part.MONTH, localDateTime.getMonth().getValue() + "")
                    .set(Part.YEAR, localDateTime.getYear() + "")
                    .build();

            //2标准时间，必须出现T
            //datetime格式为：时间类型+YY-MM-DDThh:mm:ss，其中时间类型可选，LC表示阴历，类型为空，则默认表示阳历；日期和时间之间用T分割。suggestDatetime统一表示为阳历时间，年-月-日T时:分:秒；推荐时间不保证准确，用户可以采用，也可以自行计算；suggest字段只精确到天，或者秒，大部分情况下缺失字段用当日的字段补全；阴历年、月说法补全缺失月、日字段为01，并计算阳历时间。
            //没有 R  没有  /
        } else if (!datetime.contains("T")) {
            throw CommonException.FAILURE("设置闹钟或提醒必须加上时间点");
        } else if (!datetime.contains("R") && !datetime.contains("/")) {
            LocalDateTime localDateTime = toLocalDateTime(suggestDatetime);
            cron = CronPatternBuilder
                    .of()
                    .set(Part.SECOND, localDateTime.getSecond() + "")
                    .set(Part.MINUTE, localDateTime.getMinute() + "")
                    .set(Part.HOUR, localDateTime.getHour() + "")
                    .set(Part.DAY_OF_MONTH, localDateTime.getDayOfMonth() + "")
                    .set(Part.DAY_OF_WEEK, "?")
                    .set(Part.MONTH, localDateTime.getMonth().getValue() + "")
                    .set(Part.YEAR, localDateTime.getYear() + "")
                    .build();
        } else {
            cron = hasNextTimeCron(datetime);
        }
        return cron;
    }

    public static String convertToCronExpression(String dateTimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateTimeString);
            SimpleDateFormat cronFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            return cronFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertToCronExpressionCase(String dateTimeString) {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateTimeString);
            calendar.setTime(date);
            calendar.add(Calendar.SECOND,20);
            date = calendar.getTime();
            SimpleDateFormat cronFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
            return cronFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String locationDateTimeCaseString(LocalDateTime localDateTime){
        localDateTime=localDateTime.plusSeconds(15);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr = localDateTime.format(fmt);
        return dateStr;
    }

    public static void main(String[] args) {
        String dateTimeString = "2023-06-17 09:36:03";
        String cronExpression = convertToCronExpression(dateTimeString);
        System.out.println("Cron Expression: " + cronExpression);
    }

    public static String hasNextTimeCron(IntentDto.Slot datetimeSlot) {
        String cron = slot2Cron(datetimeSlot);
        CronExpression cronExpression = buildCronExpression(cron);
        checkAfter(cronExpression);
        return cron;
    }

    public static String legalCron(IntentDto.Slot datetimeSlot) {
        String cron = slot2Cron(datetimeSlot);
        buildCronExpression(cron);
        return cron;
    }

    public static CronExpression buildCronExpression(String cron) {
        try {
            return new CronExpression(cron);
        } catch (Exception e) {
            throw CommonException.FAILURE("没有听懂你说的时间，请尽量简单点");
        }
    }

    public static void checkAfter(CronExpression cronExpression) {
        if (cronExpression.getTimeAfter(new Date()) == null) {
            throw CommonException.FAILURE("必须说比当前时间晚的时间");
        }
    }

    public static LocalDateTime toLocalDateTime(String suggestDatetime) {
        return LocalDateTime.parse(suggestDatetime);
    }

    public static Map<String, Integer> datetime2Map(String datetime) {
        String[] strArr = StrUtil.removePrefix(datetime, "R").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        int length = strArr.length;
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i = i + 2) {
            map.put(strArr[i], Integer.parseInt(strArr[i + 1]));
        }
        return map;
    }

    public static String hasNextTimeCron(String datetime) {

        String[] datetimeArr = datetime.split("\\/");
        Map<String, Integer> mapStart = null;
        Map<String, Integer> map;
        if (datetimeArr.length == 2) {
            if (datetimeArr[0].contains("T")) {
                throw CommonException.FAILURE("设置闹钟或提醒时间区间，前一个不能带时间点");
            }
            if (!datetimeArr[1].contains("T")) {
                throw CommonException.FAILURE("设置闹钟或提醒时间区间,后一个需加上时间点");
            }
            mapStart = datetime2Map(datetimeArr[0]);
            map = datetime2Map(datetimeArr[1]);
        } else {
            map = datetime2Map(datetime);
        }

        //2、？只存在于某天或某星期中，且不能同时存在(还不支持同时指定某天和星期)
        if (map.get("D") != null && map.get("WD") != null) {
            throw CommonException.FAILURE("不支持同时指定某天和星期");
        }

        CronPatternBuilder cronPatternBuilder = CronPatternBuilder
                .of()
                .set(Part.SECOND, map.get("s") + "")
                .set(Part.MINUTE, map.get("m") + "")
                .set(Part.HOUR, map.get("Th") + "");

        if (map.get("M") != null) {
            cronPatternBuilder.set(Part.MONTH, map.get("M") + "");
            if (mapStart != null && mapStart.get("M") != null) {
                cronPatternBuilder.set(Part.MONTH, builderInterval(mapStart.get("M"), map.get("M"), 12));
            }
        }
        if (map.get("Y") != null) {
            cronPatternBuilder.set(Part.YEAR, map.get("Y") + "");
            if (mapStart != null && mapStart.get("Y") != null) {
                cronPatternBuilder.set(Part.YEAR, builderInterval(mapStart.get("Y"), map.get("Y")));
            }
        } else {
            //不存在R 不存在Y  那么直接今年
            if (!datetime.contains("R")) {
                cronPatternBuilder.set(Part.YEAR, LocalDateTime.now().getYear() + "");
            }
        }

        //2、？只存在于某天或某星期中，且不能同时存在(还不支持同时指定某天和星期)
        if (map.get("D") != null) {
            cronPatternBuilder.set(Part.DAY_OF_MONTH, map.get("D") + "");
            cronPatternBuilder.set(Part.DAY_OF_WEEK, "?");
            if (mapStart != null && mapStart.get("D") != null) {
                cronPatternBuilder.set(Part.DAY_OF_MONTH, builderInterval(mapStart.get("D"), map.get("D"), 31));
            }
        }

        //1、星期里，1表示的是星期天，2表示的是星期一，以此类推，7表示的是星期六
        //2、？只存在于某天或某星期中，且不能同时存在(还不支持同时指定某天和星期)
        if (map.get("WD") != null) {
            cronPatternBuilder.set(Part.DAY_OF_MONTH, "?");
            int wd = map.get("WD") + 1;
            cronPatternBuilder.set(Part.DAY_OF_WEEK, wd == 8 ? "1" : wd + "");
            if (mapStart != null && mapStart.get("WD") != null) {
                cronPatternBuilder.set(Part.DAY_OF_WEEK, builderInterval(mapStart.get("WD") + 1, map.get("WD") + 1, 7));
            }
        }
        if (map.get("D") == null && map.get("WD") == null) {
            cronPatternBuilder.set(Part.DAY_OF_MONTH, "*");
            cronPatternBuilder.set(Part.DAY_OF_WEEK, "?");
        }
        return cronPatternBuilder.build();
    }

    public static String builderInterval(Integer start, Integer end) {
        return builderInterval(start, end, null);
    }

    public static String builderInterval(Integer start, Integer end, Integer interval) {
        if (start > end) {
            if (interval != null) {
                end += interval;
            } else {
                int temp = start;
                start = end;
                end = temp;
            }
        }
        StringBuilder str = new StringBuilder();
        for (int i = start; i <= end; i++) {
            str.append(",").append(i > interval ? i - interval : i);
        }
        return str.substring(1);
    }
}
