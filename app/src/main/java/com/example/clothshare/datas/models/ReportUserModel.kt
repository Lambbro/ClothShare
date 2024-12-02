package com.example.clothshare.datas.models

data class ReportUser(
    val reportUID: String = "",
    val userID: String = "",
    val reporterID: String = "",
    val reportTime: Long = 0,
    val reason: String = ""
)
