package com.lj.iot.biz.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneSendVo implements Serializable {

    private Long sceneId;

    private String sceneName;

    private String command;

    private List<SceneSendInfoVo> deviceList;
}
