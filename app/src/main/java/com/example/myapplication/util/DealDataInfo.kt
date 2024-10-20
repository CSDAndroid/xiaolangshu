package com.example.myapplication.util

import com.example.myapplication.data.VideoInfo
import com.example.myapplication.data.randomVideoData.VideoResponse
import com.example.myapplication.data.searchVideoData.VideoBySearch
import com.example.myapplication.data.userData.UserResponseData
import com.example.myapplication.database.UserInfo

object DealDataInfo {
    fun dealUserInfo(user: UserResponseData): UserInfo {
        val id = user.id
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
        return UserInfo(
            id, avatar, nickname, phone, password, introduction, sex, birthday, career,
            region, school, backgroundImage, null, null, null,
            null, null, null, null, null,null,
            null,null)
    }

    fun <T> dealVideoInfo(videos: List<T>): List<VideoInfo> {
        return videos.map { video ->
            when (video) {
                is VideoResponse -> VideoInfo(
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