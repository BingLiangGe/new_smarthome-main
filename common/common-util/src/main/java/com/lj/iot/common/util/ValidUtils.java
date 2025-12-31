package com.lj.iot.common.util;

import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;

import java.util.List;

public class ValidUtils {

    public static Boolean isOneLevel(Long id) {
        return id == 0L;
    }

    /**
     * 检查是否为空,为空抛异常
     *
     * @param value
     * @param errMsg
     */
    public static void isEmptyThrow(String value, String errMsg) {
        isEmptyThrow(value, CommonCodeEnum.FAILURE.getCode(), errMsg);
    }

    /**
     * 检查是否为空,为空抛异常
     *
     * @param value
     * @param code
     * @param errMsg
     */
    public static void isEmptyThrow(String value, Integer code, String errMsg) {
        if (value == null || "".equals(value)) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查Bool,为true抛异常
     *
     * @param value
     * @param errMsg
     */
    public static void isTrueThrow(Boolean value, String errMsg) {
        isTrueThrow(value, CommonCodeEnum.FAILURE.getCode(), errMsg);
    }

    /**
     * 检查Bool,为true抛异常
     *
     * @param value
     * @param code
     * @param errMsg
     */
    public static void isTrueThrow(Boolean value, Integer code, String errMsg) {
        if (value) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查Bool,为false抛异常
     *
     * @param value
     * @param errMsg
     */
    public static void isFalseThrow(Boolean value, String errMsg) {
        isFalseThrow(value, CommonCodeEnum.FAILURE.getCode(), errMsg);
    }

    /**
     * 检查Bool,为false抛异常
     *
     * @param value
     * @param code
     * @param errMsg
     */
    public static void isFalseThrow(Boolean value, Integer code, String errMsg) {
        if (!value) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查对象是否空，为空抛异常
     *
     * @param obj
     * @param errMsg
     * @throws
     */
    public static void isNullThrow(Object obj, String errMsg) {
        if (obj == null) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), errMsg);
        }
    }

    /**
     * 检查对象是否空，为空抛异常
     *
     * @param obj
     * @param code
     * @param errMsg
     */
    public static void isNullThrow(Object obj, Integer code, String errMsg) {
        if (obj == null) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查对象是否空，不为空抛异常
     *
     * @param obj
     * @param errMsg
     * @throws
     */
    public static void noNullThrow(Object obj, String errMsg) {
        if (obj != null) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), errMsg);
        }
    }

    /**
     * 检查对象是否空，不为空抛异常
     *
     * @param obj
     * @param code
     * @param errMsg
     */
    public static void noNullThrow(Object obj, Integer code, String errMsg) {
        if (obj != null) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }


    /**
     * 检查对象数据是否为空
     *
     * @param obj
     * @param errMsg
     */
    public static void arrayIsEmptyThrow(Object[] obj, String errMsg) {
        if (obj == null || obj.length == 0) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), errMsg);
        }
    }

    /**
     * 检查对象数据是否为空
     *
     * @param obj
     * @param code
     * @param errMsg
     */
    public static void arrayIsEmptyThrow(Object[] obj, Integer code, String errMsg) {
        if (obj == null || obj.length == 0) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查对象数据是否为空
     *
     * @param obj
     * @param errMsg
     */
    public static void listIsEmptyThrow(List obj, String errMsg) {
        if (obj == null || obj.size() == 0) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), errMsg);
        }
    }

    /**
     * 检查对象数据是否为空
     *
     * @param obj
     * @param code
     * @param errMsg
     */
    public static void listIsEmptyThrow(List obj, Integer code, String errMsg) {
        if (obj == null || obj.size() == 0) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查两个参数是否相等,不相等抛异常
     */
    public static void noEqualsThrow(String value1, String value2, String errMsg) {
        noEqualsThrow(value1, value2, CommonCodeEnum.FAILURE.getCode(), errMsg);
    }

    /**
     * 检查两个参数是否相等,不相等抛异常
     */
    public static void noEqualsThrow(String value1, String value2, Integer code, String errMsg) {
        if (!value1.equals(value2)) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }

    /**
     * 检查两个参数是否相等,相等抛异常
     */
    public static void isEqualsThrow(String value1, String value2, String errMsg) {
        isEqualsThrow(value1, value2, CommonCodeEnum.FAILURE.getCode(), errMsg);
    }

    /**
     * 检查两个参数是否相等,相等抛异常
     */
    public static void isEqualsThrow(String value1, String value2, Integer code, String errMsg) {
        if (value1.equals(value2)) {
            throw CommonException.INSTANCE(code, errMsg);
        }
    }
}
