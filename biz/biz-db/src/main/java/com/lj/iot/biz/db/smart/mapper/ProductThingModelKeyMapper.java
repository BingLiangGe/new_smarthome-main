package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.vo.ProductThingModelKeyVo;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 产品物模型属性按键定义表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-12
 */
public interface ProductThingModelKeyMapper extends BaseMapper<ProductThingModelKey> {

    @Select("SELECT * FROM `product_thing_model_key`  WHERE product_id=#{productId}  AND key_idx=#{keyIdx} limit 1")
    ProductThingModelKey getModeKeyByProductIdAndKeyIdx(@Param("productId") String productId,@Param("keyIdx") String keyIdx);

    IPage<ProductThingModelKey> customPage(IPage<ProductThingModelKey> page, @Param("params") ProductIdPageDto pageDto);


    List<ProductThingModelKey> customList(@Param("productId") String productId, @Param("modelId") Long modelId);
}
