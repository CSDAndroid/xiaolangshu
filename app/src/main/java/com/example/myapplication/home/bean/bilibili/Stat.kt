package com.example.myapplication.home.bean.bilibili

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stat(
    val view: Int,
    val like: Int,
    val danmaku: Int,
    val vt: Int
) : Parcelable
