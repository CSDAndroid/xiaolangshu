package com.example.myapplication.search.bean.bilibili

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchedShow(
    val switch: Boolean,
    val num: Int,
    val textSmall: String,
    val textLarge: String,
    val icon: String,
    val iconLocation: String,
    val iconWeb: String
) : Parcelable

