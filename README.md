### 技术选型

| 技术 | 版本 | 说明 |
| --- | --- | --- |
| jdk | jdk11+ | 支持11与17 |
| springboot | 2.7.1 | 建议版本固定 |
| redis | 6.0 | 目前比较稳定版本 |
| mybatis-plus | 3.5.3 |  |
| dynamic-datasource-spring-boot-starter | 3.5.1 | 多数据源 |
| easyexcel | 3.1.1 | 导出工具jar包 |
| dyvmsapi20170525 | 2.1.1 | 阿里语音通知 |
| dysmsapi20170525 | 2.0.8 | 阿里短信 |
| jpush-client | 3.6.0 | 极光推送 |
| druid-spring-boot-starter | 1.2.11 |  |
| netty-all | 4.1.78.Final | ws,mqtt |
| quartz | 2.3.2 | 任务调度 |
| mysql | 8.0 | 8.0的编码格式比5.7支持的多 |
| emqx | 5.0.8 | 开源版本 |
| minio | 20221008201100.0.0 | 目前用的老系统monio |

### 项目结构
```
java
 ├─ common
 │    ├─ common-aiui # aiui语音基础包
 │    ├─ common-base # 最基础包，放common的基础对象
 │    ├─ common-jpush # 极光推送client包
 │    ├─ common-jwt # 多点登录包
 │    ├─ common-minio # 文件系统client包
 │    ├─ common-mqtt-client # mqtt client包 
 │    ├─ common-util # 工具包 
 │    ├─ common-db-generator # 代码生成器
 │    ├─ common-quartz # quartz基础包
 │    ├─ common-redis #redis基础包
 │    ├─ common-sms #短信基础包
 │    ├─ common-vms #语音通知基础包 
 │    ├─ common-system #管理后台权限包
 │    └─ common-sso #单点鉴权基础包
 │    
 ├─ biz 
 │    ├─ biz-base # 业务基础包，主要放业务公共对象
 │    ├─ biz-db #表结构映射【只做数据库操作，不做业务处理】
 │    └─ biz-service #业务处理
 │
 │
 └─ api
      ├─ eureka  #注册中心
      ├─ app-api #app接口
      ├─ hotel-api #酒店接口     
      ├─ iot-api #iot接口（暂时不用）
      ├─ system-api #管理系统接口
      ├─ job #job
      ├─ websocket # websocket服务端
      └─ demo-api # 测试项目用    
```

### 依赖环境安装
- jdk11  [下载](https://www.oracle.com/java/technologies/downloads/#java11)
- minio  [安装教程](https://min.io/docs/minio/linux/index.html)
- redis  [安装教程](https://www.runoob.com/redis/redis-install.html)
- mysql  [安装教程](https://www.runoob.com/mysql/mysql-install.html)
- emqx [安装教程](https://www.emqx.io/zh/downloads?os=CentOS)
- nginx [安装教程](https://www.runoob.com/linux/linux-tutorial.html)

### 项目部署
- 推荐使用idea，安装lombok插件后，使用idea导入maven项目
- 点击parant install
- 上传jar包到服务器启动服务。需要启动的服务有：eureka，app-api，hotel-api，system-api，job，websocket
```
// 后端jar包部署统一格式

[root@ljapp01 service]# tree /data/service/jarServer/
/data/service/jarServer/
├── bin
│   ├── control
│   ├── jarFuncs
│   ├── monitor.sh
│   ├── run.sh
│   └── var
├── lib
├── logs
└── temp

4 directories, 5 files


/data/service/应用名/

/data/service/应用名/lib 放应用的jar包 
/data/service/应用名/bin  var 放两个变量 应用名和JAVA_OPTS
[root@ljapp01 service]# cat /data/service/jarServer/bin/var
AppName="应用名"
JAVA_OPTS="-server -Xms4g -Xmx4g -Xmn2g -Xss256k  -Duser.timezone=GMT+08  -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:SurvivorRatio=3 -XX:CMSInitiatingOccupancyFraction=80 -XX:+PrintPromotionFailure -XX:+UseCMSCompactAtFullCollection -XX:MaxTenuringThreshold=8 -XX:+CMSClassUnloadingEnabled -XX:+PrintGCDetails -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=59020 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"


单个应用启动停止命令
bash /data/service/应用名/bin/control start|stop
```
- 配置 nginx（前端部署，域名配置）


### 注意点
```
1. 根据上面项目结构的顺序，只能顺序引包，比如 common-util 可以引用common-base
   但是common-base 不能引用common-util。

2. 需要鉴权的接口用 /api/auth/作为前缀，无需鉴权或可鉴权也可不鉴权的接口用
   /api/open/ 作为前缀。
  
3. quartz中的service逻辑写在api-job 中，不要写在biz层，因为quartz需要隔离出来
   其他服务。要与job通信可以用feign或者redis。
  
4. 代码多写注释，代码比较长的，写下逻辑思路注释

5. 每次上线后需要打一个对应版本的稳定分支。修改线上bug，从对应稳定分支拉分支修改
   并合并到现有分支。
   
6.开发过程中多拉取代码，避免一次性需要解决的冲突太多。
  
```