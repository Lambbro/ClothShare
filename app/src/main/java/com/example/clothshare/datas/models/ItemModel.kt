package com.example.clothshare.datas.models
data class Item(
    val itemID: String = "",
    val imageUrl: List<String> = emptyList(),
    val name: String = "",
    val description: String = "",
    val number: Int = 0,
    val authorEmail: String = "",
    val location: String = "",
    val available: Boolean = true
)