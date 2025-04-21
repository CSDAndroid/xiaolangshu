package com.example.myapplication.home.bean.bilibili

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RcmdReason(
    val content: String?,
    val reason_type: Int?
) : Parcelable
