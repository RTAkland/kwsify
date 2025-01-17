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

# 使用

> kwsify使用二进制来发送数据包, 这就意味着你不能使用wscat之类的
> 纯文本websocket调试器来调试服务端, 我会在下面的文档中给出所有数据包的偏移值

文档地址: https://docs.rtast.cn/#/docs/kwsify/kwsify

[这里](https://repo.rtast.cn/RTAkland/rautiotransfer)是一个示例项目用于将本地的音频文件传输给其他客户端

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
    maven("https://repo.maven.rtast.cn/releases")
}

```

### 添加依赖

```kotlin
dependencies {
    implementation("cn.rtast:kwsify-api:2.0.0")  // 替换成最新版本
}
```

> 点[这里](https://pkg.rtast.cn/#/releases/cn/rtast/kwsify-api)查看所有版本(记得使用`api`模块而不是`server`模块)

### 开始使用

```kotlin
fun main() {
    val wsify = Kwsify("ws://127.0.0.1:8080")
    wsify.subscribe("test", true, object : Subscriber {
        override fun onMessage(channel: String, payload: ByteArray, packet: OutboundMessageBytesPacket) {
            // payload是完整的二进制数据包, packet.body是ByteArray形式的任意数据如果确定发送的是纯文本
            // 那么直接使用String(packet.body)即可还原出纯文本
            println(String(packet.body))
          
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


