在用苞米豆多数据源时，把放quartz表的数据源设置为主数据源，因为
spring-boot-start-quartz  默认@QuartzDatasource或者主数据源

```
srping:
   datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    dynamic:
      primary: quartz #主数据源【默认数据源】
```


注解使用   类必须实现Job   同时需要@QuartzComponent标识，执行入口为execute
```
package com.lj.iot.api.demo.quartz;

import com.lj.iot.common.quartz.anno.QuartzComponent;
import com.lj.iot.common.redis.service.ICacheService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@QuartzComponent(cron = "0/5 * * * * ?")
public class TestJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("test");
    }
}
```