package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BizUploadEntityServiceImpl implements BizUploadEntityService {
    @Resource
    IUploadEntityService uploadEntityService;

    @Resource
    IUserDeviceService userDeviceService;

    @Resource
    IHomeRoomService homeRoomService;

    @Autowired
    private IUserDeviceModeService userDeviceModeService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IUserGoodsService userGoodsService;

    @Autowired
    private IEntityAliasService entityAliasService;

    @Async
    @Override
    public void uploadEntityUserLevel(String userId, DynamicEntitiesNameEnum nameEnum) {

        if (nameEnum == DynamicEntitiesNameEnum.DeviceName) {
            deviceNameUpdateByProductType(userId);
        }

        List<UploadEntityItemDto> UploadEntityItemList = getUploadEntity(userId, nameEnum);

        if (nameEnum == DynamicEntitiesNameEnum.SceneCorpus){
            uploadEntityService.uploadCustomLevelTrigger(UploadEntityDto.builder().entityList(UploadEntityItemList)
                    .dynamicEntitiesName(nameEnum.getCode())
                    .userId(userId).build());
        }else{
            uploadEntityService.uploadCustomLevel(UploadEntityDto.builder().entityList(UploadEntityItemList)
                    .dynamicEntitiesName(nameEnum.getCode())
                    .userId(userId).build());
        }
    }

    @Override
    public void uploadDeviceNameUserLevel(String userId, Set<String> productTypeSet) {

        DynamicEntitiesNameEnum nameEnum = DynamicEntitiesNameEnum.DeviceName;

        deviceNameUpdateByProductType(userId, productTypeSet);

        List<UploadEntityItemDto> UploadEntityItemList = getUploadEntity(userId, nameEnum);

        uploadEntityService.uploadCustomLevel(UploadEntityDto.builder().entityList(UploadEntityItemList)
                .dynamicEntitiesName(nameEnum.getCode())
                .userId(userId).build());
    }

    @Override
    public void uploadEntityAppLevel(DynamicEntitiesNameEnum nameEnum) {
        uploadEntityAppLevel(nameEnum, null);
    }

    @Override
    public void uploadEntityAppLevel(DynamicEntitiesNameEnum nameEnum, String type) {
        if (nameEnum == DynamicEntitiesNameEnum.RoomName) {
            List<EntityAlias> aliasList = entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                    .attrType("room")
                    .build()));

            List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
            for (EntityAlias entityAlias : aliasList) {
                uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                        .name(entityAlias.getEntityName())
                        .alias(entityAlias.getEntityName())
                        .build());
            }
            if (uploadEntityItemDtoList.size() != 0) {
                uploadEntityService.uploadAppLevel(UploadEntityDto.builder().entityList(uploadEntityItemDtoList)
                        .dynamicEntitiesName(nameEnum.getCode()).build());
            }
        }
        if (nameEnum == DynamicEntitiesNameEnum.DeviceName) {

            List<ProductType> oneProductTypeList = productTypeService.list(new QueryWrapper<>(ProductType.builder()
                    .parentId(0L)
                    .build()));

            List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
            for (ProductType productType : oneProductTypeList) {
                List<EntityAlias> aliasList = entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                                .attrType("device")
                                .deviceType(productType.getProductType())
                                .build())).stream().sorted((a, b) -> b.getEntityName().length() - a.getEntityName().length())
                        .collect(Collectors.toList());
                StringBuilder alias = new StringBuilder();

                Set<String> stringSet = new HashSet<>();
                for (EntityAlias entityAlias : aliasList) {
                    if (!stringSet.contains(entityAlias.getEntityName())) {
                        stringSet.add(entityAlias.getEntityName());
                        alias.append("|").append(entityAlias.getEntityName());
                    }
                }
                if (!stringSet.contains(productType.getProductTypeName())) {
                    alias.append("|").append(productType.getProductTypeName());
                }

                String aliasStr = alias.toString().substring(1);
                uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                        .name(productType.getProductType())
                        .alias(aliasStr)
                        .build());

                //修改的具体类型更新
                List<UploadEntityItemDto> sub = new ArrayList<>();
                sub.add(UploadEntityItemDto.builder()
                        .name(productType.getProductType())
                        .alias(aliasStr)
                        .build());
                uploadEntityService.uploadAppLevel(UploadEntityDto.builder().entityList(sub)
                        .dynamicEntitiesName(productType.getProductType() + "_" + DynamicEntitiesNameEnum.DeviceName.getCode())
                        .build());
            }

            //设备集合更新
            if (uploadEntityItemDtoList.size() != 0) {
                uploadEntityService.uploadAppLevel(UploadEntityDto.builder().entityList(uploadEntityItemDtoList)
                        .dynamicEntitiesName(nameEnum.getCode()).build());
            }
        }
    }

    /**
     * 用户级别场景语料数据
     *
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfSceneCorpus(String userId) {
        List<Scene> sceneList = sceneService.list(new QueryWrapper<>(Scene.builder()
                .userId(userId)
                .build()));

        Set<String> stringSet = new HashSet<>();
        for (Scene scene : sceneList) {
            stringSet.addAll(List.of(scene.getCommand().split(",")));
        }
        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
        for (String s : stringSet) {
            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(s)
/*                    .alias(s)*/
                    .build());
        }
        return uploadEntityItemDtoList;
    }

    /**
     * 获取用户级别设备自定义
     *
     * @param userId
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfDeviceName(String userId) {

        List<UserDevice> userDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .userId(userId)
                .build()));
        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
        for (UserDevice userDevice : userDeviceList) {

            if (userDevice.getProductType().contains("switch") || userDevice.getProductType().contains("872")) {
                continue;
            }

            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(userDevice.getDeviceName())
                    .alias(userDevice.getCustomName())
                    .productType(userDevice.getTopProductType())
                    .build());
        }
        return uploadEntityItemDtoList;
    }

    /**
     * 用户级别房间名
     *
     * @param userId
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfRoomName(String userId) {

        List<HomeRoom> homeRoomList = homeRoomService.list(new QueryWrapper<>(HomeRoom.builder()
                .userId(userId)
                .build()));
        Set<String> stringSet = new HashSet<>();
        for (HomeRoom homeRoom : homeRoomList) {
            stringSet.add(homeRoom.getRoomName());
        }
        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
        for (String s : stringSet) {
            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(s)
                    .alias(s)
                    .build());
        }
        return uploadEntityItemDtoList;
    }

    /**
     * 用户级别模式名称
     *
     * @param userId
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfModel(String userId) {

        List<UserDeviceMode> userDeviceModeList = userDeviceModeService.list(new QueryWrapper<>(UserDeviceMode.builder()
                .userId(userId)
                .build()));

        Set<String> stringSet = new HashSet<>();
        for (UserDeviceMode userDeviceMode : userDeviceModeList) {
            stringSet.add(userDeviceMode.getModeName());
        }
        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
        for (String s : stringSet) {
            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(s)
                    .alias(s)
                    .build());
        }
        return uploadEntityItemDtoList;
    }

    /**
     * 用户级别商品名称
     *
     * @param userId
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfGoods(String userId) {

        List<UserGoods> list = userGoodsService.list(new QueryWrapper<>(UserGoods.builder()
                .userId(userId)
                .build()));

        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();

        for (UserGoods item : list) {
            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(item.getId() + "")
                    .alias(StringUtils.isBlank(item.getGoodsAlias()) ? item.getGoodsName() : item.getGoodsName() + "|" + item.getGoodsAlias())
                    .build());
        }
        return uploadEntityItemDtoList;
    }

    /**
     * 根据不同实体种类获取对应实体数据集合
     *
     * @param userId
     * @param nameEnum
     * @return
     */
    private List<UploadEntityItemDto> getUploadEntity(String userId, DynamicEntitiesNameEnum nameEnum) {
        switch (nameEnum) {
            case SceneCorpus:
                return getUploadEntityOfSceneCorpus(userId);
            case DeviceName:
                return getUploadEntityOfDeviceName(userId);
            case RoomName:
                return getUploadEntityOfRoomName(userId);
            case Model:
                return getUploadEntityOfModel(userId);
            case Goods:
                return getUploadEntityOfGoods(userId);
            default:
                return null;
        }
    }

    /**
     * 用户级产品名称
     *
     * @param userId
     */
    private void deviceNameUpdateByProductType(String userId) {

        //根据一级产品类型分类型
        List<ProductType> oneProductTypeList = productTypeService.list(new QueryWrapper<>(ProductType.builder()
                .parentId(0L)
                .build()));

        for (ProductType oneProductType : oneProductTypeList) {
            //获取对应的子类
            List<String> subProductTypeList = productTypeService.subTypeList(oneProductType.getProductTypeRay() + oneProductType.getId() + "|");
            subProductTypeList.add(oneProductType.getProductType());

            List<UserDevice> userDevices = userDeviceService.listByUserIdAndProductTypes(userId, subProductTypeList);
            if (userDevices.size() != 0) {
                Set<String> stringSet = new HashSet<>();
                for (UserDevice userDevice : userDevices) {
                    stringSet.add(userDevice.getDeviceName());
                    stringSet.add(userDevice.getCustomName());
                }
                List<UploadEntityItemDto> uploadEntityItemList = new ArrayList<>();
                for (String s : stringSet) {
                    uploadEntityItemList.add(UploadEntityItemDto.builder()
                            .name(s)
                            .alias(s)
                            .build());
                }

                uploadEntityService.uploadCustomLevel(UploadEntityDto.builder().entityList(uploadEntityItemList)
                        .dynamicEntitiesName(oneProductType.getProductType() + "_" + DynamicEntitiesNameEnum.DeviceName.getCode())
                        .userId(userId).build());
            }
        }
    }

    /**
     * 用户级产品名称
     *
     * @param userId
     */
    private void deviceNameUpdateByProductType(String userId, Set<String> productTypeSet) {

        //根据一级产品类型分类型
        List<ProductType> oneProductTypeList = productTypeService.list(new QueryWrapper<>(ProductType.builder()
                .parentId(0L)
                .build()));

        for (ProductType oneProductType : oneProductTypeList) {

            if (!productTypeSet.contains(oneProductType.getProductType())) {
                continue;
            }

            //获取对应的子类
            List<String> subProductTypeList = productTypeService.subTypeList(oneProductType.getProductTypeRay() + oneProductType.getId() + "|");
            subProductTypeList.add(oneProductType.getProductType());

            List<UserDevice> userDevices = userDeviceService.listByUserIdAndProductTypes(userId, subProductTypeList);
            if (userDevices.size() != 0) {
                Set<String> stringSet = new HashSet<>();
                for (UserDevice userDevice : userDevices) {
                    stringSet.add(userDevice.getDeviceName());
                    stringSet.add(userDevice.getCustomName());
                }
                List<UploadEntityItemDto> uploadEntityItemList = new ArrayList<>();
                for (String s : stringSet) {
                    uploadEntityItemList.add(UploadEntityItemDto.builder()
                            .name(s)
                            .alias(s)
                            .build());
                }

                uploadEntityService.uploadCustomLevel(UploadEntityDto.builder().entityList(uploadEntityItemList)
                        .dynamicEntitiesName(oneProductType.getProductType() + "_" + DynamicEntitiesNameEnum.DeviceName.getCode())
                        .userId(userId).build());
            }
        }
    }
}
