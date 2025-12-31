package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.Phonebook;
import com.lj.iot.biz.db.smart.mapper.PhonebookMapper;
import com.lj.iot.biz.db.smart.service.IPhonebookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-19
 */
@DS("smart")
@Service
public class PhonebookServiceImpl extends ServiceImpl<PhonebookMapper, Phonebook> implements IPhonebookService {

}