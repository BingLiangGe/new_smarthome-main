package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.SystemMessagesIdDto;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 联系人控制器
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/system_messages")
public class SystemMessagesController {
    @Resource
    ISosContactService sosContactService;


    @Resource
    ISystemMessagesService systemMessagesService;

    @Resource
    IHomeService homeService;

    @Autowired
    private IOfficialPhoneService officialPhoneService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    IHomeRoomService homeRoomService;

    @Resource
    ISystemMessagesTemplateService SystemMessagesTemplateService;



    @GetMapping("/find_detail")
    public CommonResultVo<ArrayList<SystemMessagesVo>> findDetail(int type,Integer homeId) {
        String actualUserId = UserDto.getUser().getActualUserId();
        SystemMessages build = SystemMessages.builder().userId(actualUserId).type(type).homeId(homeId).build();
        List<SystemMessages> list = systemMessagesService.list(new QueryWrapper<>(build).orderByDesc("create_time"));
        for (int i = 0; i < list.size(); i++) {
            String format = list.get(i).getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            list.get(i).setFomTime(format);
        }
        Map<String, List<SystemMessages>> map = list.stream().collect(Collectors.groupingBy(SystemMessages::getFomTime));
        ArrayList<SystemMessagesVo> arrayList = new ArrayList<>();
        for(String s : map.keySet()) {
            SystemMessagesVo systemMessagesVo = new SystemMessagesVo();
            systemMessagesVo.setFomTime(s);
            systemMessagesVo.setList(map.get(s));
            arrayList.add(systemMessagesVo);
        }
        Collections.reverse(arrayList);


        return CommonResultVo.SUCCESS(arrayList);
    }


    /**
     * 已读
     *
     * @return
     */
    @PostMapping("doRead")
    public CommonResultVo<String> doRead(@RequestBody @Valid SystemMessagesIdDto dto ) {
        systemMessagesService.updateById(SystemMessages.builder().id(dto.getId()).readType(1).build());
        return CommonResultVo.SUCCESS();
    }




    /**
     * 首页
     *
     * @return
     */
    @GetMapping("page")
    public CommonResultVo<ArrayList<SystemMessages>> page(Integer homeId) {
        String actualUserId = UserDto.getUser().getActualUserId();
        ArrayList<SystemMessages> arrayList = new ArrayList<>();
        List<SystemMessagesTemplate> listTemplate = SystemMessagesTemplateService.list();

        for (int i = 0; i < listTemplate.size(); i++) {
            List<SystemMessages> list = systemMessagesService.list(new QueryWrapper<>(SystemMessages.builder()
                    .type((listTemplate.get(i).getType()))
                    .homeId(homeId)
                    .userId(actualUserId)
                    .build()).orderByDesc("read_type").orderByDesc("create_time"));
           if (list.size()==0){
               //arrayList 加数据
               arrayList.add(SystemMessages.builder()
                       .homeId(homeId)
                       .id(listTemplate.get(i).getId().longValue())
                       .messages("暂时无消息")
                       .type(listTemplate.get(i).getType())
                       .userId(actualUserId)
                       .readType(1)
                       .inType(listTemplate.get(i).getInType())
                       .build());
           }else{
               arrayList.add(list.get(0));
           }

        }
        return CommonResultVo.SUCCESS(arrayList);
    }




    /**
     * 查询红点
     * @return
     */
    @GetMapping("/find_red_dot")
    public CommonResultVo<RedDot> find_red_dot() {
        String actualUserId = UserDto.getUser().getActualUserId();
        //全部类型
        SystemMessages build1 = SystemMessages.builder().userId(actualUserId).readType(0).build();
        List<SystemMessages> list1 = systemMessagesService.list(new QueryWrapper<>(build1));

        //单独网关类型
        SystemMessages build2 = SystemMessages.builder().userId(actualUserId).readType(0).type(1).build();
        List<SystemMessages> list2 = systemMessagesService.list(new QueryWrapper<>(build2));

        RedDot reddot = new RedDot();
        reddot.setSystemMessagesType(list1.size());
        reddot.setGatewayUpgradeType(list2.size());

        return CommonResultVo.SUCCESS(reddot);
    }


    /**
     * 查询红点
     * @return
     */
    @GetMapping("/red_dot")
    public CommonResultVo<Boolean> redDot() {

        Boolean reddot = false;

        String actualUserId = UserDto.getUser().getActualUserId();
        //全部类型
        SystemMessages build1 = SystemMessages.builder().userId(actualUserId).readType(0).build();
        List<SystemMessages> list1 = systemMessagesService.list(new QueryWrapper<>(build1));

        if (list1.size()>0){
            reddot = true;
        }
        return CommonResultVo.SUCCESS(reddot);
    }

}
