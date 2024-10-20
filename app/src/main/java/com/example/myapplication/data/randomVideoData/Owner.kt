package com.example.myapplication.data.randomVideoData

import java.io.Serializable

data class Owner(
    val mid: Long,
    val name: String,
    val face: String
) : Serializable
