package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.FaceUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-08-19
 */
public interface FaceUserMapper extends BaseMapper<FaceUser> {


    @Select("select face_mobile from face_user")
    public List<String> selectAllMobile();
}
