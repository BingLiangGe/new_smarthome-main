package com.lj.iot.feign.idgenerator;

import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "id-generator-api")
public interface IdGeneratorApiFeignClient {
    @PostMapping(value = "inner/next_id", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<Long> nextId();
}
