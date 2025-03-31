package com.example.myapplication.home.bean.bilibili

import java.io.Serializable

data class Stat(
    val view: Int,
    val like: Int,
    val danmaku: Int,
    val vt: Int
) : Serializable
