package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.util.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产品升级表 Mapper 接口
 *
 * @author xm
 * @since 2022-07-21
 */
public interface ProductUpgradeMapper extends BaseMapper<ProductUpgrade> {


    List<ProductUpgrade> productUpgradePageLimit(@Param("pageIndex") Integer pageIndex,@Param("pageSize") Integer pageSize,
                                                 @Param("upgrade")  ProductUpgrade productUpgrade);


    Integer productUpgradePageLimitCount( @Param("upgrade")ProductUpgrade productUpgrade);


    @Select("SELECT hard_ware_version FROM user_device  WHERE hard_ware_version != '' AND hard_ware_version != 1.1  \n" +
            " GROUP BY hard_ware_version")
    List<String> findUpgradeGroup();


    @Select("SELECT new_version,version_url,hard_ware_version FROM product_upgrade WHERE product_id=#{product_id} \n" +
            " and hard_ware_version=#{hardWareVersion}  AND new_version > #{softWareVersion}  ORDER BY new_version DESC LIMIT 1")
    ProductUpgrade findNewUpgradeByProduct(@Param("product_id") String product_id,@Param("hardWareVersion") String hardWareVersion,@Param("softWareVersion") String softWareVersion);

    IPage<ProductUpgrade> newPage(IPage<ProductUpgrade> pageDto, @Param("params") ProductPageDto pDto);

    ProductUpgrade findByMaxId(@Param("productId") String productId);
}
