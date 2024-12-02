package com.example.clothshare.datas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseSingleton {
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
}