package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.MusicOrderPageVo;
import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.lj.iot.biz.db.smart.service.IMusicMenuService;
import com.lj.iot.biz.db.smart.service.IMusicOrderService;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.biz.service.BizMusicOrderService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 音乐管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/music")
public class AuthMusicController {

    @Autowired
    private IMusicMenuService musicMenuService;

    @Autowired
    private IMusicProductService musicProductService;

    @Autowired
    private IMusicOrderService musicOrderService;

    @Autowired
    private BizMusicOrderService bizMusicOrderService;

    /**
     * 铃声列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("music:menu:page")
    @RequestMapping("/menu_page")
    public CommonResultVo<IPage<MusicMenu>> menuPage(PageDto pageDto) {
        return CommonResultVo.SUCCESS(musicMenuService.page(PageUtil.page(pageDto)));
    }

    /**
     * 添加铃声
     *
     * @return
     */
    @CustomPermissions("music:menu:add")
    @PostMapping("/menu_add")
    public CommonResultVo<String> menuAdd(@Valid MusicMenuAddDto paramDto) {

        musicMenuService.save(MusicMenu.builder()
                .musicName(paramDto.getMusicName())
                .musicUrl(paramDto.getMusicUrl())
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除铃声
     *
     * @return
     */
    @CustomPermissions("music:menu:del")
    @PostMapping("/menu_del")
    public CommonResultVo<String> menuDel(@Valid IdDto idDto) {
        musicMenuService.removeById(idDto.getId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 音乐产品列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("music:product:page")
    @RequestMapping("/product_page")
    public CommonResultVo<IPage<MusicProduct>> productPage(PageDto pageDto) {
        return CommonResultVo.SUCCESS(musicProductService.customPage(pageDto));
    }

    /**
     * 添加音乐
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("music:product:add")
    @PostMapping("/product_add")
    public CommonResultVo<String> productAdd(@Valid MusicProductAddDto paramDto) {
        musicProductService.save(MusicProduct.builder()
                .musicName(paramDto.getMusicName())
                .count(paramDto.getCount())
                .price(paramDto.getPrice())
                .coverUrl(paramDto.getCoverUrl())
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 音乐编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("music:product:edit")
    @PostMapping("/product_edit")
    public CommonResultVo<String> productEdit(@Valid MusicProductEditDto paramDto) {
        musicProductService.update(MusicProduct.builder()
                .musicName(paramDto.getMusicName())
                .count(paramDto.getCount())
                .price(paramDto.getPrice())
                .coverUrl(paramDto.getCoverUrl())
                .build(), new QueryWrapper<>(MusicProduct.builder()
                .id(paramDto.getId())
                .build()));
        return CommonResultVo.SUCCESS();
    }

    /**
     * 音乐订单列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("music:order:page")
    @RequestMapping("/order_page")
    public CommonResultVo<IPage<MusicOrderPageVo>> orderPage(MusicOrderPageDto pageDto) {
        return CommonResultVo.SUCCESS(musicOrderService.customPage(pageDto));
    }

    /**
     * 订单支付成功
     *
     * @param dto
     * @return
     */
    @CustomPermissions("music:order:complete")
    @RequestMapping("/complete")
    public CommonResultVo<String> complete(@Valid CompleteMusicOrderDto dto) {
        bizMusicOrderService.completeOrder(dto.getOrderNo(), dto.getTransactionId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 设备激活
     *
     * @param dto
     * @return
     */
    @CustomPermissions("music:order:active")
    @RequestMapping("/active")
    public CommonResultVo<String> active(@Valid ActiveMusicOrderDto dto) {
        MusicOrder musicOrder = musicOrderService.getOne(new QueryWrapper<>(MusicOrder.builder()
                .deviceId(dto.getDeviceId())
                .orderNo(dto.getOrderNo())
                .build()));
        ValidUtils.isNullThrow(musicOrder, "数据不存在");
        bizMusicOrderService.active(musicOrder.getDeviceId(), musicOrder.getUserId());
        return CommonResultVo.SUCCESS();
    }
}
