package com.example.myapplication.data.pictureData

import java.io.Serializable

data class Picture1(
    val author: String,
    val avatar: String?,
    var collections: Int,
    var description: String?,
    val id: Long,
    var likes: Int,
    val phone: String,
    val picture: String,
) : Serializable