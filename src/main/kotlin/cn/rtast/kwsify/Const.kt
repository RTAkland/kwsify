/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/30
 */


package cn.rtast.kwsify

import com.google.gson.Gson
import com.google.gson.GsonBuilder


val gson: Gson = GsonBuilder()
    .disableHtmlEscaping()
    .create()
