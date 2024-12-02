package com.example.clothshare.datas.repositories

import android.net.Uri
import android.util.Log
import com.example.clothshare.datas.FirebaseSingleton
import com.example.clothshare.datas.models.Item
import com.google.firebase.firestore.toObject

class ItemRepository {
    private val items = FirebaseSingleton.firestore.collection("items")
    private val users = FirebaseSingleton.firestore.collection("users")

    fun getAllItem(callback: (List<Item>) -> Unit) {
        items.get()
            .addOnSuccessListener { querySnapshot ->
                val itemList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject<Item>()
                }
                callback(itemList)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error getting items", exception)
                callback(emptyList())
            }
    }

    fun getAllItemOfUser(email: String, callback: (List<Item>) -> Unit) {
        items.whereEqualTo("authorEmail", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val itemList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject<Item>()
                    }
                callback(itemList)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error getting items", exception)
                callback(emptyList())
            }
    }

    // Get a single item
    fun getItem(itemID: String, callback: (Item?) -> Unit){
        items.document(itemID)
            .get()
            .addOnSuccessListener { document ->
                val item = document.toObject<Item>()
                callback(item)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error getting item", exception)
                callback(null)
            }
    }

    fun updateItem(item: Item, callback: (Boolean) -> Unit) {
        items.document(item.itemID)
            .update(
                "name", item.name,
                "description", item.description,
                "number", item.number,
                "location", item.location,
                "available", item.available,
                "imageUrl", item.imageUrl
            )
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error updating item", exception)
                callback(false)
            }
    }

    fun createItem(item: Item, callback: (Boolean) -> Unit) {
        val itemWithGeneratedID = item.copy(itemID = items.document().id)
        items.document(itemWithGeneratedID.itemID).set(itemWithGeneratedID)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error creating item", exception)
                callback(false)
            }

    }

    fun uploadImages(imageUris: List<Uri>, callback: (List<String>) -> Unit) {
        val imageUrls = mutableListOf<String>()
        val storageRef = FirebaseSingleton.storage.reference
        val imagesRef = storageRef.child("images")
        var successCount = 0
        var failureCount = 0

        // Duyệt qua tất cả các ảnh cần upload
        imageUris.forEachIndexed { index, uri ->
            val imageRef = imagesRef.child("${System.currentTimeMillis()}_$index.jpg")
            val uploadTask = imageRef.putFile(uri)

            // Tiến hành upload ảnh
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.e("ItemRepository", "Error uploading image", it)
                        failureCount++
                    }
                    // Trả về null nếu upload thất bại
                    return@continueWithTask null
                }
                // Trả về URL tải xuống nếu upload thành công
                return@continueWithTask imageRef.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result.toString()
                        imageUrls.add(downloadUri)
                        successCount++
                    } else {
                        // Log lỗi nếu tải xuống URL không thành công
                        Log.e("ItemRepository", "Error getting download URL")
                        failureCount++
                    }

                    // Kiểm tra nếu tất cả ảnh đã được xử lý (bao gồm cả thành công và thất bại)
                    if (successCount + failureCount == imageUris.size) {
                        callback(imageUrls) // Trả lại danh sách ảnh đã upload
                    }
                }
                // Xử lý trường hợp upload thất bại
                .addOnFailureListener { exception ->
                    Log.e("ItemRepository", "Error uploading image", exception)
                    failureCount++
                    // Kiểm tra nếu tất cả ảnh đã được xử lý (bao gồm cả thất bại)
                    if (successCount + failureCount == imageUris.size) {
                        callback(imageUrls) // Trả lại danh sách ảnh đã upload
                    }
                }
        }
    }

    fun deleteItem(itemID: String, callback: (Boolean) -> Unit) {
        items.document(itemID)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("ItemRepository", "Error deleting item", exception)
                callback(false)
            }
    }
}