配置（非必须）

```
mqtt:
  client:
    enabled: true
    keep-alive-interval: 5
    host: "47.107.86.36"
    port: 1883
    username: tom
    password: cat
    client-id: myclient123
    default-topic: topic
```

发送消息

```
    MQTT.publish("topic"，“消息”);
```
