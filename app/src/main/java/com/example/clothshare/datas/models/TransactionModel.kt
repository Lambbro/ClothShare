package com.example.clothshare.datas.models

data class Transaction(
    val transactionID: String = "",
    val itemID: String = "",
    val requesterEmail: String = "",
    val receiverEmail: String = "",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val requestTime: Long = 0,
    val pickUpTime: Long = 0,
    val quantities: Int = 0,
    val isCompleted: Boolean = false
)

enum class TransactionStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}