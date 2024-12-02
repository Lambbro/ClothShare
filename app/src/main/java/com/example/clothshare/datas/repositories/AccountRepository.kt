package com.example.clothshare.datas.repositories

import android.util.Log
import androidx.navigation.NavHostController
import com.example.clothshare.datas.FirebaseSingleton

class AccountRepository (private val navController: NavHostController) {
    private val auth = FirebaseSingleton.auth

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("MainScreen") {
                        popUpTo("AccountScreen") {inclusive = true}
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Đã có lỗi xảy ra"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AccountRepository", "Registration failed", exception)
            }
    }

    fun login (email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("MainScreen") {
                        popUpTo("AccountScreen") {inclusive = true}
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Đã có lỗi xảy ra"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AccountRepository", "Login failed", exception)
            }
    }

    fun signOut () {
        auth.signOut()
        navController.navigate("AccountScreen") {
            popUpTo("MainScreen") {inclusive = true}
        }
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?:""
    }
}