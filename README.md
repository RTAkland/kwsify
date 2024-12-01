<div align="center">
  <img src="https://rtakland.github.io/Static/static/kwsify.png" alt="head" width="128">
  <br>
  <h3><em>基于websocket的实时通信服务</em></h3>
  <br>
  <img src="https://rtakland.github.io/Static/static/kotlin/made-with-kotlin.svg" alt="madeWithKotlin">
  <br>
  <img src="https://img.shields.io/github/actions/workflow/status/RTAkland/kwsify/main.yml" alt="buildState">
  <img src="https://img.shields.io/badge/Kotlin-v2.0.21-pink?logo=Kotlin" alt="KtV">
  <img src="https://img.shields.io/badge/LICENSE-Apache2.0-green?logo=apache" alt="license">
  <img src="https://img.shields.io/badge/JVM-1.8+-red?logo=Openjdk&link=https://a.com" alt="jvm">

</div>

# 目录

<!-- TOC -->
* [目录](#目录)
* [使用](#使用)
  * [服务端](#服务端)
    * [构建](#构建)
  * [SDK](#sdk)
    * [添加Maven仓库](#添加maven仓库)
    * [添加依赖](#添加依赖)
    * [开始使用](#开始使用)
* [开源](#开源)
* [鸣谢](#鸣谢)
<!-- TOC -->

# 使用

## 服务端

### 构建

```shell
$ chmod +x ./gradlew  # 可选
$ ./gradlew buildShadowJar
```

```shell
$ java -jar kwsify.jar [--port 8989]
```

## SDK

### 添加Maven仓库

```kotlin
repositories {
    maven("https://repo.rtast.cn/api/v4/projects/49/packages/maven")
}

```

### 添加依赖

```kotlin
dependencies {
    implementation("cn.rtast.kwsify:api:1.1.1")  // 替换成最新版本
}
```

> 点[这里](https://repo.rtast.cn/RTAkland/kwsify/-/packages)查看所有版本(记得使用`api`模块而不是`kwsify`模块)

### 开始使用

```kotlin
fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {
        override fun onMessage(channel: String, payload: String, packet: OutboundMessagePacket) {
            println(packet.body)
            Thread.sleep(500L)
            wsify.publish("test", "114514")
        }

        override fun onClosed(channel: String) {
            println("closed")
            wsify.reconnect()
        }
    })
    Thread.sleep(1000L)
    wsify.publish("test", "114514")
}
```

# 开源

- 本项目以[Apache-2.0](./LICENSE)许可开源, 即:
    - 你可以直接使用该项目提供的功能, 无需任何授权
    - 你可以在**注明来源版权信息**的情况下对源代码进行任意分发和修改以及衍生

# 鸣谢

<div>

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" alt="JetBrainsIcon" width="128">

<a href="https://www.jetbrains.com/opensource/"><code>JetBrains Open Source</code></a> 提供的强大IDE支持

</div>


