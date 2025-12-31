package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.UserGoodsAddDto;
import com.lj.iot.biz.base.dto.UserGoodsEditDto;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.biz.db.smart.service.IUserGoodsService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.biz.service.BizUserGoodsService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizUserGoodsServiceImpl implements BizUserGoodsService {

    @Autowired
    private IUserGoodsService userGoodsService;

    @Autowired
    private BizUploadEntityService bizUploadEntityService;

    @Override
    public IPage<UserGoods> customPage(PageDto pageDto, String userId) {

        return userGoodsService.page(PageUtil.page(pageDto), new QueryWrapper<>(
                UserGoods.builder()
                        .userId(userId)
                        .build()));
    }

    @Override
    public IPage<UserGoods> customPage(PageDto pageDto, Long hotelId, String userId) {
        return userGoodsService.customPage(pageDto, hotelId, userId);
    }

    @Override
    public IPage<SeachDeviceVo> customPageUserDevice(PageDto pageDto) {
        return userGoodsService.customPageUserDevice(pageDto);
    }

    @Override
    public void add(UserGoodsAddDto dto, String userId) {

        List<UserGoods> goodsList = userGoodsService.list(
                new QueryWrapper<>(UserGoods.builder().userId(userId).build()));
        goodsNameHandle(null, dto.getGoodsName(), dto.getGoodsAlias(), goodsList);

        userGoodsService.save(UserGoods.builder()
                .userId(userId)
                .goodsName(dto.getGoodsName())
                .goodsAlias(dto.getGoodsAlias())
                .unit(dto.getUnit())
                .state(dto.getState())
                .quantity(dto.getQuantity())
                .images(dto.getImages())
                .build());

        //上传实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Goods);

    }

    @Override
    public void add(UserGoodsAddDto dto, Long hotelId, String userId) {
        List<UserGoods> goodsList = userGoodsService.list(
                new QueryWrapper<>(UserGoods.builder()
                        .hotelId(hotelId)
                        .userId(userId).build()));
        goodsNameHandle(null, dto.getGoodsName(), dto.getGoodsAlias(), goodsList);

        UserGoods one = userGoodsService.getOne(new QueryWrapper<>(UserGoods.builder().goodsName(dto.getGoodsName()).hotelId(hotelId).build()));
        ValidUtils.noNullThrow(one, "商品已存在");
        userGoodsService.save(UserGoods.builder()
                .userId(userId)
                .hotelId(hotelId)
                .goodsName(dto.getGoodsName())
                .goodsAlias(dto.getGoodsAlias())
                .unit(dto.getUnit())
                .state(dto.getState())
                .quantity(dto.getQuantity())
                .images(dto.getImages())
                .build());

        //上传实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Goods);
    }

    //商品名字去重
    private void goodsNameHandle(Long id, String goodsName, String goodsAlias, List<UserGoods> goodsList) {

        String name = "";
        if (StringUtils.isNotBlank(goodsName)) {
            name = name + goodsName;
        }
        if (StringUtils.isNotBlank(goodsAlias)) {
            name = name + "," + goodsAlias;
        }

        for (UserGoods userGoods : goodsList) {
            if (id != null || userGoods.getId() == id) {
                continue;
            }
            String dbName = "";
            for (String s : name.split(",")) {
                goodsName = userGoods.getGoodsName();
                goodsAlias = userGoods.getGoodsAlias();
                if (StringUtils.isNotBlank(goodsName)) {
                    dbName = dbName + goodsName;
                }
                if (StringUtils.isNotBlank(goodsAlias)) {
                    dbName = dbName + "," + goodsAlias;
                }
                for (String dbs : dbName.split(",")) {
                    ValidUtils.isTrueThrow(dbs.equals(s), "商品名字重复");
                }
            }

        }
    }

    @Override
    public void edit(UserGoodsEditDto dto, String userId) {
        UserGoods userGoods = userGoodsService.getOne(new QueryWrapper<>(UserGoods.builder()
                .id(dto.getId())
                .userId(userId)
                .build()));

        ValidUtils.isNullThrow(userGoods, "数据不存在");


        List<UserGoods> goodsList = userGoodsService.list(
                new QueryWrapper<>(UserGoods.builder().userId(userId).build()));
        goodsNameHandle(dto.getId(), dto.getGoodsName(), dto.getGoodsAlias(), goodsList);

        userGoodsService.updateById(UserGoods.builder()
                .id(userGoods.getId())
                .userId(userId)
                .goodsName(dto.getGoodsName())
                .goodsAlias(dto.getGoodsAlias())
                .unit(dto.getUnit())
                .state(dto.getState())
                .quantity(dto.getQuantity())
                .images(dto.getImages())
                .build());

        //上传实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Goods);
    }

    @Override
    public void edit(UserGoodsEditDto dto, Long hotelId, String userId) {
        UserGoods userGoods = userGoodsService.getOne(new QueryWrapper<>(UserGoods.builder()
                .id(dto.getId())
                .userId(userId)
                .build()));

        ValidUtils.isNullThrow(userGoods, "数据不存在");


        List<UserGoods> goodsList = userGoodsService.list(
                new QueryWrapper<>(UserGoods.builder()
                        .hotelId(hotelId)
                        .userId(userId).build()));

        goodsNameHandle(dto.getId(), dto.getGoodsName(), dto.getGoodsAlias(), goodsList);

        userGoodsService.updateById(UserGoods.builder()
                .id(userGoods.getId())
                .userId(userId)
                .goodsName(dto.getGoodsName())
                .goodsAlias(dto.getGoodsAlias())
                .unit(dto.getUnit())
                .state(dto.getState())
                .quantity(dto.getQuantity())
                .images(dto.getImages())
                .build());

        //上传实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Goods);
    }

    @Override
    public void delete(IdDto dto, String userId) {
        UserGoods userGoods = userGoodsService.getOne(new QueryWrapper<>(UserGoods.builder()
                .id(dto.getId())
                .userId(userId)
                .build()));

        ValidUtils.isNullThrow(userGoods, "数据不存在");

        userGoodsService.removeById(userGoods.getId());
        //上传实体
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Goods);
    }
}
