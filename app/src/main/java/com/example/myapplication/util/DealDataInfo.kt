package com.example.myapplication.util

import com.example.myapplication.common.bean.VideoInfo
import com.example.myapplication.search.bean.bilibili.VideoBySearch
import com.example.myapplication.common.bean.UserResponseData
import com.example.myapplication.home.bean.bilibili.VideoProfile
import com.example.myapplication.storage.db.entity.Account

object DealDataInfo {
    fun dealUserInfo(user: UserResponseData): Account {
        val avatar = user.avatar
        val nickname = user.nickname
        val phone = user.phone
        val password = user.password
        val introduction = user.bio
        val sex = user.gender
        val birthday = user.birthday
        val career = user.occupation
        val region = user.region
        val school = user.school
        val backgroundImage = user.backgroundImage
        return Account(phone, avatar, nickname, password, introduction, sex, birthday, career,
            region, school, backgroundImage)
    }

    fun <T> dealVideoInfo(videos: List<T>): List<VideoInfo> {
        return videos.map { video ->
            when (video) {
                is VideoProfile -> VideoInfo(
                    aid = video.id,
                    cid = video.cid,
                    like = video.stat.like,
                    image = video.pic,
                    avatar = video.owner.face,
                    collection = video.stat.view,
                    nickname = video.owner.name,
                    description = video.title
                )

                is VideoBySearch -> VideoInfo(
                    aid = video.aid ?: 0,
                    cid = video.id ?: 0,
                    like = video.like ?: 0,
                    image = "https:${video.pic}",
                    avatar = video.upic ?: "",
                    collection = video.favorites ?: 0,
                    nickname = video.author ?: "",
                    description = video.description ?: ""
                )

                else -> VideoInfo(0, 0, 0, "", "", 0, "", "")
            }
        }
    }
}