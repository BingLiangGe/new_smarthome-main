package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.WatchSosVo;
import com.lj.iot.biz.db.smart.entity.WatchSos;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 手表sos记录 服务类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface IWatchSosService extends IService<WatchSos> {


     void daring(String deviceId, Integer type, String value);

    List<WatchSosVo> getSosList( String deviceId,String date, Integer type);
}
