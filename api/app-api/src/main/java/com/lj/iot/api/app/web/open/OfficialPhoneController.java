package com.lj.iot.api.app.web.open;

import com.lj.iot.biz.db.smart.entity.OfficialPhone;
import com.lj.iot.biz.db.smart.service.IOfficialPhoneService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * 官方电话接口
 * 
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/open/official_phone")
public class OfficialPhoneController {
    @Autowired
    IOfficialPhoneService officialPhoneService;

    /**
     * 查看所有官方电话
     */
    @GetMapping("findOfficialPhoneList")
    public CommonResultVo<List<OfficialPhone>> findOfficialPhoneList() {
        return CommonResultVo.SUCCESS(officialPhoneService.list());
    }
}
