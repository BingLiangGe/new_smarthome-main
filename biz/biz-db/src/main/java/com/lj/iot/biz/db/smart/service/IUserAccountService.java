package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.UserAccountEditDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.common.base.dto.PageDto;

/**
 * 用户账号表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IUserAccountService extends IService<UserAccount> {

    /**
     * 保存修改用户信息数据
     *
     * @param dto 用户信息数据
     * @return 成功 true 失败false
     */
    UserAccount edit(UserAccountEditDto dto, String userId);


    IPage<UserAccount> customPage(PageDto pageDto);



    void deleteByIdAndCache(String id);

    UserAccount editByIdAndCache(UserAccount userAccount);

    UserAccount getByIdCache(String id);
}
