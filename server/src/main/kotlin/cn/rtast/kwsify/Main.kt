/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/1
 */


package cn.rtast.kwsify

import cn.rtast.kwsify.util.KWsifyServer
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

fun main(args: Array<String>) {
    val parser = ArgParser("kwsify-cli")
    val port by parser.option(ArgType.Int, shortName = "p", description = "Port number").required()
    parser.parse(args)
    KWsifyServer(port).start()
}