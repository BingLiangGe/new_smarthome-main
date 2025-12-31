package com.lj.iot.api.system.web.auth;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductAddDto;
import com.lj.iot.biz.base.dto.ProductEditDto;
import com.lj.iot.biz.base.dto.ProductIdDto;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.service.BizProductService;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product")
public class AuthProductController {

    @Autowired
    private BizProductService bizProductService;

    /**
     * 产品表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<Product>> productPage(ProductPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizProductService.customPage(pageDto));
    }

    /**
     * 新增产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:add")
    @PostMapping("/add")
    public CommonResultVo<String> productAdd(@Valid ProductAddDto paramDto) {
        bizProductService.add(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> productEdit(@Valid ProductEditDto paramDto) {
        bizProductService.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除产品
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> productDel(@Valid ProductIdDto paramDto) {
        bizProductService.delete(paramDto);
        return CommonResultVo.SUCCESS();
    }

    public static void main(String[] args) {
        String a="{\"properties\":[{\"name\":\"电源\",\"value\":\"0\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"identifier\":\"powerstate\"},{\"name\":\"工作模式\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"制冷\",\"2\":\"除湿\",\"3\":\"送风\",\"4\":\"制热\"}},\"identifier\":\"workmode\"},{\"name\":\"当前温度\",\"value\":\"23\",\"dataType\":{\"type\":\"int\",\"specs\":{\"max\":\"30\",\"min\":\"16\",\"step\":\"1\"}},\"identifier\":\"temperature\"},{\"name\":\"风速\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"低速\",\"2\":\"中速\",\"3\":\"高速\"}},\"identifier\":\"fanspeed\"},{\"name\":\"风向\",\"value\":\"0\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"风向1\",\"2\":\"风向2\",\"3\":\"风向3\",\"4\":\"风向4\"}},\"identifier\":\"airdirection\"}]}";
        System.out.println(JSON.parseObject(a, ThingModel.class));
    }

}
