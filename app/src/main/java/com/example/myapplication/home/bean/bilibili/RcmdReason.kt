package com.example.myapplication.home.bean.bilibili

import java.io.Serializable

data class RcmdReason(
    val content: String?,
    val reason_type: Int?
) : Serializable
