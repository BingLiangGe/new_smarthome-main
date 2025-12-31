package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.ProductListItemVo;
import com.lj.iot.biz.base.vo.ProductVo;
import com.lj.iot.biz.db.smart.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 
 * 领捷产品表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface ProductMapper extends BaseMapper<Product> {



    @Select("SELECT\n" +
            "  p.`product_id`,\n" +
            "  p.`product_type`,\n" +
            "  p.`product_name`,\n" +
            "  p.`signal_type`,\n" +
            "  p.`images_url`,\n" +
            "  p.`relation_device_type_id`\n" +
            "FROM\n" +
            "  product p\n" +
            "WHERE p.`is_app_show` = 0 AND p.`signal_type`=#{type} ORDER BY p.`create_time` DESC")
    List<ProductListItemVo> getProductListItem(String type);


    @Select("SELECT signal_type FROM product GROUP BY signal_type order by signal_type asc")
    List<String> getProductSignType();

    @Select("SELECT product_id,product_name FROM product WHERE (signal_type='MASTER' OR signal_type='MESH');")
    List<ProductVo> getProductList();


    IPage<Product> customPage(IPage<Product> page, @Param("params") ProductPageDto pageDto);
}
