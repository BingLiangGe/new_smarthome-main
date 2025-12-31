package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.db.smart.entity.HomeUser;
import com.lj.iot.biz.db.smart.mapper.HomeUserMapper;
import com.lj.iot.biz.db.smart.service.IHomeUserService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 家和用户关联表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class HomeUserServiceImpl extends ServiceImpl<HomeUserMapper, HomeUser> implements IHomeUserService {

    @Override
    public IPage<HomeUser> customPage(HomeUserPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public List<String> getEditMemberUserIdsByHomeId(Long homeId) {
        return this.baseMapper.getEditMemberUserIdsByHomeId(homeId);
    }

    @Override
    public List<HomeUserVo> findHomeUserList(Long homeId,String userId) {
        return this.baseMapper.findHomeUserList(homeId,userId);
    }

    @Override
    public List<HomeDataVo> listHome(String userId) {
        return this.baseMapper.listHome(userId);
    }

    @Override
    public HomeUser defaultHome(String userId) {
        return this.baseMapper.defaultHome(userId);
    }

    @Override
    public HomeUser findByHomeIdAndUserId(Long homeId, String uId) {
        return this.getOne(new QueryWrapper<>(HomeUser.builder()
                .homeId(homeId)
                .memberUserId(uId).build()));
    }

    @Override
    public Long getHomeIdById(Long homeUserId) {
        HomeUser homeUser = this.getById(homeUserId);
        if (homeUser != null) {
            return homeUser.getHomeId();
        }
        return null;
    }

    @CacheEvict(value = "common-cache", key = "'HomeUser:'+#homeUser.homeId+'.'+#homeUser.memberUserId")
    public void deleteAndCache(HomeUser homeUser) {
        this.removeById(homeUser.getId());
    }

    @CachePut(value = "common-cache", key = "'HomeUser:'+#homeUser.homeId+'.'+#homeUser.memberUserId", unless = "#result == null")
    public HomeUser addAndCache(HomeUser homeUser) {
        this.save(homeUser);
        return this.getById(homeUser.getId());
    }

    @CachePut(value = "common-cache", key = "'HomeUser:'+#homeUser.homeId+'.'+#homeUser.memberUserId", unless = "#result == null")
    @Override
    public HomeUser editAndCache(HomeUser homeUser, String type) {
        this.updateById(HomeUser.builder()
                .id(homeUser.getId())
                .type(type)
                .build());
        return this.getById(homeUser.getId());
    }

    //@Cacheable(value = "common-cache", key = "'HomeUser:'+#homeId+'.'+#memberUserId", unless = "#result == null")
    public HomeUser getOneCache(Long homeId, String memberUserId) {
        return this.getOne(new QueryWrapper<>(HomeUser.builder()
                .homeId(homeId)
                .memberUserId(memberUserId)
                .build()));
    }

}
