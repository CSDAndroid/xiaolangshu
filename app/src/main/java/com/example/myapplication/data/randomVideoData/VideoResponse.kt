package com.example.myapplication.data.randomVideoData

import java.io.Serializable

data class VideoResponse(
    val id: Long,
    val bvid: String,
    val cid: Long,
    val goto: String,
    val uri: String,
    val pic: String,
    val pic_4_3: String?,
    val title: String,
    val duration: Int,
    val pubdate: Long,
    val owner: Owner,
    val stat: Stat,
    val av_feature: String?,
    val is_followed: Int,
    val rcmd_reason: RcmdReason?,
    val show_info: Int,
    val track_id: String?,
    val pos: Int,
    val room_info: Any?,
    val ogv_info: Any?,
    val business_info: Any?,
    val is_stock: Int,
    val enable_vt: Int,
    val vt_display: String?,
    val dislike_switch: Int,
    val dislike_switch_pc: Int
) : Serializable