package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.vo.ProductThingModelKeyVo;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;

import java.util.List;

/**
 * <p>
 * 产品物模型属性按键定义表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-09-12
 */
public interface IProductThingModelKeyService extends IService<ProductThingModelKey> {

    /**
     * 获取按键通过产品id及按键id
     * @param productId
     * @param keyIdx
     * @return
     */
    ProductThingModelKey getModeKeyByProductIdAndKeyIdx(String productId,String keyIdx);

    IPage<ProductThingModelKey> customPage(ProductIdPageDto pageDto);

    /**
     * 根据产品id获取按键数据
     *
     * @param
     * @return
     */
    List<ProductThingModelKey> keyList(String productId, Long modelId);

    ProductThingModelKey getProductThingModelKey(String productId, Long modelId, String keyCode);

    ProductThingModelKey getProductThingModelKey(String productId, Long modelId, String identify, Integer value);
}
