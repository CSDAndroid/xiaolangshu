package com.example.myapplication.data

import com.example.myapplication.data.pictureData.Picture1

sealed class Item {
    data class Picture(val picture: Picture1): Item()
    data class Video(val video: VideoInfo): Item()
}