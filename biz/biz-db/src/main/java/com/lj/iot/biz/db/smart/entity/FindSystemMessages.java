package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-02-24
 */

public class FindSystemMessages implements Serializable {


    private String time;
    private int type;
    private ArrayList<SystemMessages> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<SystemMessages> getData() {
        return data;
    }

    public void setData(ArrayList<SystemMessages> data) {
        this.data = data;
    }
}
