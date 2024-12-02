package com.example.clothshare.datas.repositories

import android.net.Uri
import android.util.Log
import com.example.clothshare.datas.FirebaseSingleton
import com.example.clothshare.datas.models.Item
import com.example.clothshare.datas.models.User

class UserRepository {
    private val users = FirebaseSingleton.firestore.collection("users")

    fun getAllUser(callback: (List<User>?) -> Unit) {
        users.get()
            .addOnSuccessListener { result ->
                val userList = result.documents.mapNotNull { it.toObject(User::class.java) }
                callback(userList)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error getting users: ${e.message}", e)
                callback(null)
            }
    }

    fun getUserById(userId: String, callback: (User?) -> Unit){
        users.document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error getting user: ${e.message}", e)
                callback(null)
            }
    }
    fun getFavorList(email: String, callback: (List<Item>?) -> Unit) {
        getUserByEmail(email) { user ->
            if (user != null) {
                val favorList = user.favoriteClothes
                callback(favorList)
            } else {
                callback(null)
            }

        }
    }
    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        users
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    val user = result.documents[0].toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error getting user: ${e.message}", e)
                callback(null)
            }
    }

    fun createUser(user: User, callback: (Boolean) -> Unit) {
        val userWithGeneratedID = user.copy(userID = users.document().id)
        users.document(userWithGeneratedID.userID).set(userWithGeneratedID)
            .addOnSuccessListener {
                callback(true)  // Thêm thành công
            }
            .addOnFailureListener { exception ->
                Log.e("UserRepository", "Error creating user: ${exception.message}", exception)
                callback(false)
            }
    }

    fun saveUserAvatar(avatarUri: Uri, callback: (Boolean, String) -> Unit) {
        val storage = FirebaseSingleton.storage
        val avatarRef = storage.reference.child("avatars/${System.currentTimeMillis()}.jpg")

        avatarRef.putFile(avatarUri)
            .addOnSuccessListener {
                avatarRef.downloadUrl.addOnSuccessListener { url ->
                    callback(true, url.toString())  // Gửi lại URL ảnh tải lên
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error uploading avatar: ${e.message}", e)
                callback(false, "")  // Gửi lỗi nếu tải ảnh thất bại
            }
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        users.document(user.userID)
            .update(
                "name", user.name,
                "phoneNumber", user.phoneNumber,
                "avatarUrl", user.avatarUrl,
                "favoriteClothes", user.favoriteClothes,
            )
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error updating user: ${e.message}", e)
                callback(false)
            }
    }

    fun updateUserAvatar(userId: String, avatarUri: String, callback: (Boolean) -> Unit) {
        users.document(userId)
            .update("avatarUrl", avatarUri)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error updating user avatar: ${e.message}", e)
                callback(false)
            }
    }

    fun addToFavoriteList(email: String, item: Item, callback: (Boolean) -> Unit) {
        getUserByEmail(email) { user ->
            if (user != null) {
                val favoriteList = user.favoriteClothes.toMutableList()

                // Kiểm tra xem item đã tồn tại trong danh sách chưa
                val isItemExists = favoriteList.any { it.itemID == item.itemID }

                if (!isItemExists) {
                    favoriteList.add(item)
                    val updatedUser = user.copy(favoriteClothes = favoriteList)
                    updateUser(updatedUser) { success ->
                        callback(success)
                    }
                } else {
                    // Gọi callback nhưng không thay đổi gì vì đã tồn tại
                    callback(false)
                }
            } else {
                callback(false)
            }
        }
    }


    fun removeFromFavoriteList(email: String, item: Item, callback: (Boolean) -> Unit) {
        getUserByEmail(email) { user ->
            if (user != null) {
                val favoriteList = user.favoriteClothes.toMutableList()

                // Kiểm tra xem item có tồn tại trong danh sách hay không
                val isItemExists = favoriteList.any { it.itemID == item.itemID }

                if (isItemExists) {
                    favoriteList.removeAll { it.itemID == item.itemID } // Xóa item theo ID
                    val updatedUser = user.copy(favoriteClothes = favoriteList)
                    updateUser(updatedUser) { success ->
                        callback(success)
                    }
                } else {
                    // Item không tồn tại, gọi callback với giá trị false
                    callback(false)
                }
            } else {
                callback(false)
            }
        }
    }


    fun deleteUser(userId: String, callback: (Boolean) -> Unit) {
        users.document(userId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error deleting user: ${e.message}", e)
                callback(false)
            }
    }
}
