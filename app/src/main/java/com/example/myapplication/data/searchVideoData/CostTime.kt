package com.example.myapplication.data.searchVideoData

import com.squareup.moshi.Json

data class CostTime(
    val total: String,
    @Json(name = "fetch_lexicon") val fetchLexicon: String,
    @Json(name = "params_check") val paramsCheck: String,
    @Json(name = "is_risk_query") val isRiskQuery: String,
    @Json(name = "illegal_handler") val illegalHandler: String,
    @Json(name = "main_handler") val mainHandler: String,
    @Json(name = "get_upuser_live_status") val getUserLiveStatus: String,
    @Json(name = "mysql_request") val mysqlRequest: String,
    @Json(name = "as_request_format") val asRequestFormat: String,
    @Json(name = "as_request") val asRequest: String,
    @Json(name = "deserialize_response") val deserializeResponse: String,
    @Json(name = "as_response_format") val asResponseFormat: String
)

