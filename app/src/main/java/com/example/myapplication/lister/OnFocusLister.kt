package com.example.myapplication.lister

interface OnFocusLister {
    fun onFocus(followPhone: String, phone: String)
    fun isFocus(followPhone: String, phone: String): Boolean
}