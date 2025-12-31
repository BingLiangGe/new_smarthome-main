package com.lj.iot.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeUserJoinHandleDto;
import com.lj.iot.biz.base.dto.HomeUserJoinPageDto;
import com.lj.iot.biz.base.dto.InviteUserDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeUserJoinService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.HomeUserJoinActionEnum;
import com.lj.iot.biz.service.enums.HomeUserJoinStateEnum;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.checkerframework.checker.units.qual.Prefix.one;

@Service
public class BizHomeUserJoinServiceImpl implements BizHomeUserJoinService {

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHomeUserService homeUserService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IHomeUserJoinService homeUserJoinService;

    @Resource
    ISystemMessagesService systemMessagesService;


    /**
     * 自己家数据
     *
     * @param pageDto
     * @param userId
     * @return
     */
    public IPage<HomeUserJoinVo> pageMyHome(PageDto pageDto, String userId) {

        HomeUserJoinPageDto homeUserJoinPageDto = new HomeUserJoinPageDto();
        homeUserJoinPageDto.setCurrent(pageDto.getCurrent());
        homeUserJoinPageDto.setSize(pageDto.getSize());
        homeUserJoinPageDto.setSearch(pageDto.getSearch());
        homeUserJoinPageDto.setUserId(userId);
        return homeUserJoinService.customPage(homeUserJoinPageDto);
    }

    /**
     * 其他家数据
     *
     * @param pageDto
     * @param userId
     * @return
     */
    @Override
    public IPage<HomeUserJoinVo> pageOtherHome(PageDto pageDto, String userId) {
        HomeUserJoinPageDto homeUserJoinPageDto = new HomeUserJoinPageDto();
        homeUserJoinPageDto.setCurrent(pageDto.getCurrent());
        homeUserJoinPageDto.setSize(pageDto.getSize());
        homeUserJoinPageDto.setSearch(pageDto.getSearch());
        homeUserJoinPageDto.setMemberUserId(userId);
        return homeUserJoinService.customPage(homeUserJoinPageDto);
    }

    @Override
    public void invite(InviteUserDto dto, String userId) {
        UserAccount byId = userAccountService.getById(userId);
        String str = byId.getNickname()+"用户:"+"邀请了你加入"+dto.getHomeId()+"家庭";


        Integer homeId = (int) dto.getHomeId();
        Home home = homeService.getById(dto.getHomeId());
        ValidUtils.isNullThrow(home, "家庭不存在");

        ValidUtils.noEqualsThrow(home.getUserId(), userId, "非家庭管理员");

        UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .mobile(dto.getUserMobile())
                .type(AccountTypeEnum.MASTER.getCode())
                .build()));
        ValidUtils.isNullThrow(userAccount, "该手机还未注册app账号,请先注册");

        HomeUser homeUser = homeUserService.getOneCache(home.getId(), userAccount.getId());
        ValidUtils.noNullThrow(homeUser, "已经在当前家庭");

        HomeUserJoin homeUserJoin = homeUserJoinService.getOne(new QueryWrapper<>(HomeUserJoin.builder()
                .homeId(home.getId())
                .memberUserId(userAccount.getId())
                .state(HomeUserJoinStateEnum.PENDING.getCode())
                .action(HomeUserJoinActionEnum.INVITE.getCode())
                .build()));


        //已存在推送ws消息
        if (homeUserJoin != null) {
            homeUserJoin.setHomeName(home.getHomeName());
            homeUserJoin.setUserName(byId.getNickname());
            bizWsPublishService.publish(WsResultVo.SUCCESS(
                    userAccount.getId(),
                    home.getId(),
                    RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_INVITE,
                    homeUserJoin));
            systemMessagesService.save(SystemMessages.builder().userId(userAccount.getId()).homeName(home.getHomeName()).readType(0).homeId(homeId).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).messages(str).type(4).roomId(0).inType("邀请好友").joinId(homeUserJoin.getId()).build());
            return;
        }

        homeUserJoin = HomeUserJoin.builder()
                .userId(home.getUserId())
                .homeId(home.getId())
                .memberUserId(userAccount.getId())
                .memberMobile(userAccount.getMobile())
                .homeName(home.getHomeName())
                .userName(byId.getNickname())
                .action(HomeUserJoinActionEnum.INVITE.getCode())
                .state(HomeUserJoinStateEnum.PENDING.getCode())
                .build();

        homeUserJoinService.save(homeUserJoin);

        bizWsPublishService.publish(WsResultVo.SUCCESS(
                userAccount.getId(),
                home.getId(),
                RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_INVITE,
                homeUserJoin));
        systemMessagesService.save(SystemMessages.builder().userId(userAccount.getId()).homeName(home.getHomeName()).readType(0).homeId(homeId).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).messages(str).type(4).inType("邀请好友").joinId(homeUserJoin.getId()).build());

    }

    @DSTransactional
    @Override
    public Long handleInvite(HomeUserJoinHandleDto dto, String userId) {
        HomeUserJoin homeUserJoin = homeUserJoinService.getOne(
                new QueryWrapper<>(HomeUserJoin.builder()
                        .id(dto.getId())
                        .memberUserId(userId)
                        .homeId(dto.getHomeId())
                        .action(HomeUserJoinActionEnum.INVITE.getCode())
                        .build()));
        ValidUtils.isNullThrow(homeUserJoin, "数据不存在");
        ValidUtils.isFalseThrow(homeUserJoin.getState().equals(HomeUserJoinStateEnum.PENDING.getCode()), "数据已处理，不要重复提交");

        homeUserJoin.setState(dto.getFlag() ? HomeUserJoinStateEnum.AGREE.getCode() : HomeUserJoinStateEnum.REFUSE.getCode());

        homeUserJoinService.updateById(homeUserJoin);

        if (dto.getFlag()) {
            homeUserService.addAndCache(HomeUser.builder()
                    .userId(homeUserJoin.getUserId())
                    .homeId(homeUserJoin.getHomeId())
                    .isMain(false)
                    .memberUserId(homeUserJoin.getMemberUserId())
                    .memberMobile(homeUserJoin.getMemberMobile())
                    .isDefaultHome(false)
                    .type(AccountTypeEnum.MEMBER.getCode())
                    .build());
        }

        bizWsPublishService.publish(WsResultVo.SUCCESS(
                homeUserJoin.getUserId(),
                homeUserJoin.getHomeId(),
                RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_INVITE_HANDLE,
                homeUserJoin));
        List<SystemMessages> list = systemMessagesService.list(new QueryWrapper<>(SystemMessages.builder().joinId(dto.getId()).build()));

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setReadType(1);
            systemMessagesService.updateById(list.get(i));
        }


        return homeUserJoin.getHomeId();
    }

    @Override
    public void apply(HomeIdDto dto, String userId) {
        UserAccount byId = userAccountService.getById(userId);
        Home home = homeService.getById(dto.getHomeId());
        ValidUtils.isNullThrow(home, "您申请加入的家庭不存在");

        UserAccount userAccount = userAccountService.getById(userId);
        ValidUtils.isNullThrow(userAccount, "当前用户不存在");

        HomeUser homeUser = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder()
                .homeId(home.getId())
                .memberUserId(userAccount.getId())
                .build()));
        ValidUtils.noNullThrow(homeUser, "已经加入当前家庭");

        HomeUserJoin homeUserJoin = homeUserJoinService.getOne(new QueryWrapper<>(HomeUserJoin.builder()
                .homeId(home.getId())
                .memberUserId(userAccount.getId())
                .state(HomeUserJoinStateEnum.PENDING.getCode())
                .action(HomeUserJoinActionEnum.APPLY.getCode())
                .build()));
        //已存在推送ws消息
        if (homeUserJoin != null) {
            homeUserJoin.setHomeName(home.getHomeName());
            homeUserJoin.setUserName(byId.getNickname());
            bizWsPublishService.publish(WsResultVo.SUCCESS(
                    home.getUserId(),
                    home.getId(),
                    RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_APPLY,
                    homeUserJoin));
            return;
        }

        homeUserJoin = HomeUserJoin.builder()
                .userId(home.getUserId())
                .homeId(home.getId())
                .homeName(home.getHomeName())
                .userName(byId.getNickname())
                .memberUserId(userAccount.getId())
                .memberMobile(userAccount.getMobile())
                .action(HomeUserJoinActionEnum.APPLY.getCode())
                .state(HomeUserJoinStateEnum.PENDING.getCode())
                .build();

        homeUserJoinService.save(homeUserJoin);

        bizWsPublishService.publish(WsResultVo.SUCCESS(
                home.getUserId(),
                home.getId(),
                RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_APPLY,
                homeUserJoin));
    }

    @Override
    public void handleApply(HomeUserJoinHandleDto dto, String userId) {
        HomeUserJoin homeUserJoin = homeUserJoinService.getOne(
                new QueryWrapper<>(HomeUserJoin.builder()
                        .id(dto.getId())
                        .userId(userId)
                        .homeId(dto.getHomeId())
                        .action(HomeUserJoinActionEnum.APPLY.getCode())
                        .build()));
        ValidUtils.isNullThrow(homeUserJoin, "数据不存在");

        ValidUtils.isFalseThrow(homeUserJoin.getState().equals(HomeUserJoinStateEnum.PENDING.getCode()), "数据已处理，不要重复提交");

        homeUserJoin.setState(dto.getFlag() ? HomeUserJoinStateEnum.AGREE.getCode() : HomeUserJoinStateEnum.REFUSE.getCode());

        homeUserJoinService.updateById(homeUserJoin);

        if (dto.getFlag()) {
            homeUserService.save(HomeUser.builder()
                    .userId(homeUserJoin.getUserId())
                    .homeId(homeUserJoin.getHomeId())
                    .isMain(false)
                    .memberUserId(homeUserJoin.getMemberUserId())
                    .memberMobile(homeUserJoin.getMemberMobile())
                    .type(AccountTypeEnum.MEMBER.getCode())
                    .isDefaultHome(false)
                    .build());
        }

        bizWsPublishService.publish(WsResultVo.SUCCESS(
                homeUserJoin.getMemberUserId(),
                homeUserJoin.getHomeId(),
                RedisTopicConstant.TOPIC_CHANNEL_JOIN_FAMILY_APPLY_HANDLE,
                homeUserJoin));
    }
}
