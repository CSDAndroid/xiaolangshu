package com.example.myapplication.lister

interface ImageDownLoadCallBack {
    fun onDownLoadSuccess(text: String)
    fun onDownLoadFailed(text: String)
}