# Kwsify Docs

> Hi, this is the docs of [kwsify](https://github.com/RTAkland/kwsify)

> 这是英文文档, 中文文档点[这里](README.md)

<!-- TOC -->
* [Kwsify Docs](#kwsify-docs)
* [The name of project](#the-name-of-project)
* [Test websocket server easily](#test-websocket-server-easily)
* [Working principle](#working-principle)
<!-- TOC -->

# The name of project

> I saw a project called [kwsify](https://github.com/alash3al/wsify)
> is used to impl realtime communication, but it's written in go,
> I get used to programming with [Kotlin](https://kotl.in), so i
> published this project.

# Test websocket server easily

> A tool called `wscat` can help you easily to test
> websocket. If you want to use this tool to test
> server, you need to install the package manager of
> `nodejs` called `npm`. Install the tool with command:
> `npm install -g wscat`. As completing the installation,
> open the terminal/cmd then type: `wscat -c ws://localhost:5050/subscribe`
> to connect the websocket server

# Working principle

> As soon as server started, it will set up a websocket to
> receive connection, it has two endpoints to impl different
> functions. The subscribers need to provide the channel name
> and the client id. They are used to verify and isolate identities