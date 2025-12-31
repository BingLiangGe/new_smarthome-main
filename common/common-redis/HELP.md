配置（非必须）

```
redis:
  cacheNames:
    param-config: 300 #（单位秒）
    server-cache-10: 10 #（可设置多个）
```

缓存组使用示例【因为默认的缓存注解不支持过期时间，所以加了个配置，使默认的缓存注解有失效】

```
    @Cacheable(value = "param-config", key = "#key", unless = "#result == null")
    @Override
    public String getString(String key) {
        return "ok";
    }
```

发送队列消息示例

```
    @Autowired
    private ICacheService cacheService;

    @Test
    void redisPush(){
        cacheService.convertAndSend("topic1","ss");
    }
```

监听消息示例

```
package com.lj.iot.api.demo.redis;

import com.lj.iot.common.redis.anno.RedisComponent;
import com.lj.iot.common.redis.anno.RedisListener;

@RedisComponent
public class TestListener {

    @RedisListener("topic1")
    public void topic1(String msg){
        System.out.println(msg);
    }

    @RedisListener("topic2")
    public void topic2(String msg){
        System.out.println(msg);
    }
}
```