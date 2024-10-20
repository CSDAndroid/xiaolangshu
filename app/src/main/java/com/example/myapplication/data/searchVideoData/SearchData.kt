package com.example.myapplication.data.searchVideoData

data class SearchData<T>(
    val seid: String,
    val page: Int,
    val pagesize: Int,
    val numResults: Int,
    val numPages: Int,
    val suggestKeyword: String,
    val rqtType: String,
    val costTime: CostTime,
    val expList: Map<String, Boolean>,
    val eggHit: Int,
    val pageInfo: Map<String, PageInfo>,
    val topTlist: Map<String, Int>,
    val showColumn: Int,
    val showModuleList: List<String>,
    val appDisplayOption: Map<String, Int>,
    val inBlackKey: Int,
    val inWhiteKey: Int,
    val result: List<Result<T>>,
    val isSearchPageGrayed: Int
)


