package com.example.myapplication.home.bean.bilibili

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Owner(
    val mid: Long,
    val name: String,
    val face: String
) : Parcelable
