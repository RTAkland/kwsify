# kwsify

基于websocket的实时通信服务

# 使用

> 在[Release](https://github.com/RTAkland/kwsify/releases/latest/)中下载最新版本的jar
> 然后使用`java -jar kwsify.jar`运行, 默认监听地址为`0.0.0.0:5050`使用命令行参数来修改默认值
> `java -jar kwsify.jar 127.0.0.1 6666` 这个启动方式将监听地址设置为了127.0.0.1:6666

> 客户端连接需要使用websocket客户端连接, 你可以使用`wscat`来简单的调试, 使用`npm install -g wscat`
> 来安装wscat, 安装完成后使用`wscat -c ws://localhost:5050/subscribe`然后发送`{"action":"subscribe","channel":"test"}`
> 来订阅`test`频道, 客户端任何原因失去连接都会使其取消订阅, 再开启一个ws客户端连接到`/publish`端点
> 发送`{"channel":"test","payload":"this is a test message"}`然后
> 即可在第一个客户端的消息列表收到`{"channel":"test","payload":"this is a test message"}`


# 开源

- 本项目以[Apache-2.0](./LICENSE)许可开源, 即:
    - 你可以直接使用该项目提供的功能, 无需任何授权
    - 你可以在**注明来源版权信息**的情况下对源代码进行任意分发和修改以及衍生

# 鸣谢

* [JetBrains Open Source](https://www.jetbrains.com/opensource/) 项目提供的IDE支持.

