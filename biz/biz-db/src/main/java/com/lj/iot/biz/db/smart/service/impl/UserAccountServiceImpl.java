package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.UserAccountEditDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.mapper.UserAccountMapper;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

/**
 * 用户账号表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

    @Override
    public UserAccount edit(UserAccountEditDto dto, String userId) {
        this.updateById(UserAccount.builder()
                .id(userId)
                .nickname(dto.getNickname())
                .avatarUrl(dto.getAvatarUrl())
                .build());
        return this.getById(userId);
    }

    @Override
    public IPage<UserAccount> customPage(PageDto pageDto) {
        IPage<UserAccount> page = PageUtil.page(pageDto);
        return this.baseMapper.customPage(page, pageDto);
    }

    @CachePut(value = "common-cache", key = "'UserAccount:'+#id", unless = "#result == null")
    @Override
    public void deleteByIdAndCache(String id) {
        this.removeById(id);
    }

    @CachePut(value = "common-cache", key = "'UserAccount:'+#userAccount.id", unless = "#result == null")
    @Override
    public UserAccount editByIdAndCache(UserAccount userAccount) {
        this.updateById(userAccount);
        return this.getById(userAccount.getId());
    }

    @CachePut(value = "common-cache", key = "'UserAccount:'+#id", unless = "#result == null")
    @Override
    public UserAccount getByIdCache(String id) {
        return this.getById(id);
    }

}
