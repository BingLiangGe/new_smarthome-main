package com.lj.iot.common.util;

import java.util.List;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ToPinYin {

    public static void main(String[] args) {
        System.out.println(getPinYin("在线模式"));
//        String file = "I:/myData/qq_data/3442175060/FileRecv/一3学生名单.xlsx";
//        ExcelUtil.readBySax(file, 0, createRowHandler());
    }

    private static RowHandler createRowHandler() {
        return new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowlist) {
                if (rowIndex > 1) {
                    String name1 = (String) rowlist.get(1);
                    String name2 = (String) rowlist.get(9);
                    Console.log("[{}] [{}] {}\t\t{}\t\t{}\t\t{}", sheetIndex, rowIndex, name1, getPinYin(name1), name2,
                            getPinYin(name2));
                }

            }
        };
    }


    public static String getPinYin(String src) {
        if (StrUtil.isBlank(src) || "null".equals(src)) {
            return "null";
        }
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        // t3是全部的拼音，不带声调
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);

        // format是全部的拼音并且带声调
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
//					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], format);
                    t4 += t2[0] + " ";
                } else {
                    t4 += java.lang.Character.toString(t1[i]);
                }
            }
            return t4.substring(0,t4.length()-1);
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }
}
