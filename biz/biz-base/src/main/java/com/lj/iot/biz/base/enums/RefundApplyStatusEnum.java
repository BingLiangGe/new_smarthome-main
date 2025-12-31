package com.lj.iot.biz.base.enums;

/**
 * 申请退款状态枚举类
 */
public enum RefundApplyStatusEnum {
    UNTREATED(0, "未处理"),

    REJECT(1, "驳回"),

    ACCEPT(2, "接受"),

    COMPLETED(3, "处理完成"),

    CLOSE(5, "关闭");

    private Integer code;

    /**
     * 描述
     */
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RefundApplyStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RefundApplyStatusEnum parse(String code) {
        for (RefundApplyStatusEnum item : RefundApplyStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
