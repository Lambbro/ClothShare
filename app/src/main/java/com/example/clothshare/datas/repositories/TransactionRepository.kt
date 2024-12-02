package com.example.clothshare.datas.repositories

import android.util.Log
import com.example.clothshare.datas.FirebaseSingleton
import com.example.clothshare.datas.models.Transaction
import com.example.clothshare.datas.models.TransactionStatus

class TransactionRepository (

) {
    private val users = FirebaseSingleton.firestore.collection("users")
    private val transactions = FirebaseSingleton.firestore.collection("transactions")
    private val items = FirebaseSingleton.firestore.collection("items")

    fun getAllByRequester(email: String, callback: (List<Transaction>?) -> Unit) {
        transactions.whereEqualTo("requesterEmail", email)
            .get()
            .addOnSuccessListener { result ->
                val transactionList = result.documents.mapNotNull { it.toObject(Transaction::class.java) }
                val sortedList = transactionList.sortedWith { t1, t2 ->
                    when {
                        t1.status == TransactionStatus.PENDING && t2.status != TransactionStatus.PENDING -> -1
                        t1.status != TransactionStatus.PENDING && t2.status == TransactionStatus.PENDING -> 1
                        t1.status == TransactionStatus.ACCEPTED && t2.status != TransactionStatus.ACCEPTED -> -1
                        t1.status != TransactionStatus.ACCEPTED && t2.status == TransactionStatus.ACCEPTED -> 1
                        else -> 0
                    }
                }

                callback(sortedList)
            }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error getting transactions: ${e.message}", e)
                callback(null)
            }
    }

    fun getAllByReceiver(email: String, callback: (List<Transaction>?) -> Unit) {
        transactions.whereEqualTo("receiverEmail", email)
            .get()
            .addOnSuccessListener { result ->
                val transactionList = result.documents
                    .mapNotNull { it.toObject(Transaction::class.java) }
                    //.filter { it.status != TransactionStatus.REJECTED }
                val sortedList = transactionList.sortedWith { t1, t2 ->
                    when {
                        t1.status == TransactionStatus.PENDING && t2.status != TransactionStatus.PENDING -> -1
                        t1.status != TransactionStatus.PENDING && t2.status == TransactionStatus.PENDING -> 1
                        t1.status == TransactionStatus.ACCEPTED && t2.status != TransactionStatus.ACCEPTED -> -1
                        t1.status != TransactionStatus.ACCEPTED && t2.status == TransactionStatus.ACCEPTED -> 1
                        else -> 0
                    }
                }

                callback(sortedList)
            }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error getting transactions: ${e.message}", e)
                callback(null)
            }
    }

    fun getTransactionById(transactionId: String, callback: (Transaction?) -> Unit) {
        transactions.document(transactionId)
            .get()
            .addOnSuccessListener { document ->
                val transaction = document.toObject(Transaction::class.java)
                callback(transaction)
                }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error getting transaction: ${e.message}", e)
                callback(null)
            }
    }

    fun changeStatus(transactionId: String, newStatus: TransactionStatus, callback: (Boolean) -> Unit) {
        transactions.document(transactionId)
            .update("status", newStatus)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error updating transaction status: ${e.message}", e)
                callback(false)
            }
    }

    fun createTransaction(transaction: Transaction, callback: (Boolean) -> Unit) {
        val transactionWithGeneratedID = transaction.copy(transactionID = transactions.document().id)
        transactions.document(transactionWithGeneratedID.transactionID).set(transactionWithGeneratedID)
            .addOnSuccessListener {
                callback(true)  // Thêm thành công
            }
            .addOnFailureListener { exception ->
                Log.e("TransactionRepository", "Error creating transaction: ${exception.message}", exception)
                callback(false)
            }
    }

    fun updateTransaction(transaction: Transaction, callback: (Boolean) -> Unit) {
        transactions.document(transaction.transactionID).set(transaction)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error updating transaction: ${e.message}", e)
            }
    }

    fun deleteTransaction(transactionId: String, callback: (Boolean) -> Unit) {
        transactions.document(transactionId).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("TransactionRepository", "Error deleting transaction: ${e.message}", e)
                callback(false)
            }
    }

}