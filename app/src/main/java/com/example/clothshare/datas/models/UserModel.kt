package com.example.clothshare.datas.models

data class User(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val favoriteClothes: List<Item> = listOf()
)