package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.SpeechRecord;
import com.lj.iot.biz.db.smart.mapper.SpeechRecordMapper;
import com.lj.iot.biz.db.smart.service.ISpeechRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 语音识别记录 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-10-28
 */
@DS("smart")
@Service
public class SpeechRecordServiceImpl extends ServiceImpl<SpeechRecordMapper, SpeechRecord> implements ISpeechRecordService {

}
