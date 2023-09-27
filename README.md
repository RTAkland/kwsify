# kwsify

<div align="center">
  <img src="https://rtakland.github.io/Static/static/kwsify.png" alt="head" width="128">
  <br>
  <h3><em>基于websocket的实时通信服务</em></h3>
  <br>
  <img src="https://rtakland.github.io/Static/static/kotlin/made-with-kotlin.svg" alt="madeWithKotlin">
  <br>
  <img src="https://img.shields.io/github/actions/workflow/status/RTAkland/kwsify/main.yml" alt="buildState">
  <img src="https://img.shields.io/badge/Kotlin-v1.8.22-pink?logo=Kotlin" alt="KtV">
  <img src="https://img.shields.io/badge/LICENSE-Apache20-green?logo=apache" alt="license">
  <img src="https://img.shields.io/badge/JVM-1.8+-red?logo=Openjdk&link=https://a.com" alt="jvm">

</div>



<!-- TOC -->
* [kwsify](#kwsify)
* [使用](#使用)
  * [Subscribe](#subscribe)
* [开发](#开发)
* [构建](#构建)
  * [Linux](#linux)
  * [Windows](#windows)
* [开源](#开源)
* [鸣谢](#鸣谢)
<!-- TOC -->

# 使用

```shell
$ java -jar kwsify.jar --help  # 获取帮助
```

> 在[Release](https://github.com/RTAkland/kwsify/releases/latest/)中下载最新版本的jar使用以下命令运行

```shell
$ # 默认的监听地址为: 0.0.0.0:5050
$ java -jar kwsify.jar [--host 0.0.0.0] [--port 6060]
```

## Subscribe

> 使用Websocket连接到服务器

> ***返回的数据中的msgType表示的是消息还是通知如果是Message则表明这个返回值是发布者发布的消息***
> ***如果是Notify则标明是系统通知***

```shell
$ # 订阅者
$ wscat -c ws://localhost:5050/subscribe
$ > {"action":"Subscribe", "channel":"channel-1"}
$ # {"action":"Subscribe", "channel":"channel-1", "clientId":"this is a client id"}
$ < {"timestamp":...,"msgType":"Notify","msgBody":"..."}
$ # 取消订阅
$ > {"action":"Unsubscribe", "channel":"channel-1"}
$ < {"timestamp":...,"msgType":"Notify","msgBody":"..."}
```

* 如果不指定`clientId`则会自动生成一个配置文件长度的随机字符串并返回
* 订阅者发送最少参数为 `{"action":"subscribe", "channel":"test"}`你可以在此基础添加clientId
* 如果你忘记了随机生成的`clientId`那么你可以在`订阅者模式`将`action`设置为 `clientid` (不区分大小写)

```shell
$ # 发布者
$ wscat -c ws://localhost:5050/publish
$ > {"action":"Publish", "channel":"channel-1", "payload":"this is a test message", "clientId":"this is a client id"}
$ < {"timestamp":...,"msgType":"Notify","msgBody":"Successfully sent to the client!"}
$ # 如果没有用户订阅你指定的 `channel` 和 `clientId`那么服务器将不会返回任何消息
```

> 然后就会在订阅者的消息列表中找到`this is a test message`

# 开发

> 使用 `git clone https://github.com/RTAkland/kwsify.git` 克隆到本地然后使用`IDEA`任意版本打开项目文件夹
> 然后就可以开发了, 写好你的代码中后提交`PR`吧~
> ***贡献代码请注意代码规范, 低质量、不遵循Kotlin代码规规范PR请求将会直接被关闭***

# 构建

## Linux

> 你需要先将`gradlew`授予可执行权限: `chmod +x ./gradlew`
> 使用 `./gradlew shadowJar` 来进行构建, 产出物在`build/libs/*-all.jar` (文件名需要包含-all字样的jar文件才可以运行)

## Windows

> 同上不过不需要提前将`gradlew.bat` 授予权限
> 打开`cmd`或者`powershell`使用 `.\gradlew.bat shadowJar`进行构建

# 开源

- 本项目以[Apache-2.0](./LICENSE)许可开源, 即:
    - 你可以直接使用该项目提供的功能, 无需任何授权
    - 你可以在**注明来源版权信息**的情况下对源代码进行任意分发和修改以及衍生

# 鸣谢

* [JetBrains Open Source](https://www.jetbrains.com/opensource/) 项目提供的IDE支持.

