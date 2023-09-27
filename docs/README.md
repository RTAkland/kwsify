# Kwsify Docs

> 这里是项目 [kwsify](https://github.com/RTAkland/kwsify)的中文文档

> If you want to read the english docs, please click [here](README-en.md)

<!-- TOC -->
* [Kwsify Docs](#kwsify-docs)
* [项目命名的由来](#项目命名的由来)
* [快捷的测试方法](#快捷的测试方法)
* [项目的工作原理](#项目的工作原理)
  * [`会话`的解释](#会话的解释)
<!-- TOC -->

# 项目命名的由来

> 在GitHub上看大到了一个叫[wsify](https://github.com/alash3al/wsify)
> 的项目也是用ws来实现实时通信, 但是是用[go](https://go.dev/)
> 写的, 我就想用 [kotlin](https://kotl.in)来实现顺便练一下手

# 快捷的测试方法

> 使用`wscat`工具或者其他网页上的工具来快速测试你的服务器
> 如果要使用`wscat` 你需要安装 `nodejs`的包管理工具 `npm`
> 使用: `npm install -g wscat`来安装`wscat`, 安装完成后
> 用 `wscat -c ws://localhost:5050/subscribe` 来连接

# 项目的工作原理

> 建立一个ws服务器接收连接, 分多个端点实现不同的功能, 订阅者发送需要订阅的频道和客户端ID
> 客户端ID用来验证是否为同一个用户组, 频道用来区分用户组内的不同群组.

> 发布者可以指定频道和客户端id来将消息发送给所有在相同频道和相同客户端id 的`会话`(订阅者)

## `会话`的解释

> 会话就是指一个`Websocket`连接, 本质上是一个线程, 订阅者在连接到服务器并且指定了
> 频道和客户端id之后这个`线程`就会被保存进一个列表内, 如果订阅者断开连接了之后就会
> 将其从列表中删除
