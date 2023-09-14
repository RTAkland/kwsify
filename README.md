# kwsify

基于websocket的实时通信服务

# 使用

```shell
$ java -jar kwsify.jar --help  # 获取帮助
```

> 在[Release](https://github.com/RTAkland/kwsify/releases/latest/)中下载最新版本的jar使用以下命令运行

```shell
$ # 默认的监听地址为: 0.0.0.0:5050git 
$ java -jar kwsify.jar [--host 0.0.0.0] [--port 6060]
```

```shell
$ # 订阅者
$ wscat -c ws://localhost:5050/subscribe
$ {"action":"Subscribe", "channel":"channel-1"}
$ # 取消订阅
$ {"action":"Unsubscribe", "channel":"channel-1"}
```

```shell
$ # 发布者
$ wscat -c ws://localhost:5050/publish
$ {"action":"Publish", "channel":"channel-1", "payload":"this is a test message"}
```

> 然后就会在订阅者的消息列表中找到`this is a test message`

# 开源

- 本项目以[Apache-2.0](./LICENSE)许可开源, 即:
    - 你可以直接使用该项目提供的功能, 无需任何授权
    - 你可以在**注明来源版权信息**的情况下对源代码进行任意分发和修改以及衍生

# 鸣谢

* [JetBrains Open Source](https://www.jetbrains.com/opensource/) 项目提供的IDE支持.

