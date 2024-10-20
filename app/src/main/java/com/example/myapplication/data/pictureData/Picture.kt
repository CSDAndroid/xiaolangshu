package com.example.myapplication.data.pictureData

import java.io.Serializable

data class Picture(
    val author: String,
    val avatar: String?,
    val collections: Int,
    val description: String,
    val likes: Int,
    val phone: String,
    val picture: String,
) : Serializable
