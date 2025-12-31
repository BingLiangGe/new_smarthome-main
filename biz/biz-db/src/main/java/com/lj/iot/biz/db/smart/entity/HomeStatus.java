package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 房间实时状态
 * </p>
 *
 * @author 皓
 * @since 2023-02-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HomeStatus implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 离线数量
     */
    private Long offLine;

    /**
     * 在线数量
     */
    private Long onLine;

    /**
     * 温度
     */
    private String temperature;

}
