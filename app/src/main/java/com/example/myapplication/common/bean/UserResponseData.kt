package com.example.myapplication.common.bean

data class UserResponseData(
    val createBy: String? = null,
    val createTime: String? = null,
    val updateBy: String? = null,
    val updateTime: String? = null,
    val remark: String? = null,
    val id: Int,
    val nickname: String,
    val password: String,
    val bio: String?,
    val avatar: String? = null,
    val gender: String? = null,
    val createdAt: String? = null,
    val phone: String,
    val birthday: String? = null,
    val occupation: String? = null,
    val region: String? = null,
    val school: String? = null,
    val backgroundImage: String? = null
)