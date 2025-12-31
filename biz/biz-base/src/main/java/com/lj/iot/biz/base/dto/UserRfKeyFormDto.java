package com.lj.iot.biz.base.dto;

import com.lj.iot.biz.base.vo.UserRfKeyVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRfKeyFormDto {

    private List<UserRfKeyVo> userRfKeyVoList;
}
