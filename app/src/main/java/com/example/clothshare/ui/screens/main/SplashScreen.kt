package com.example.clothshare.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clothshare.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val currentUser = auth.currentUser

    LaunchedEffect(true) {
        delay(2000)
        if (currentUser != null) {
            navHostController.navigate("MainScreen") {
                popUpTo("MainScreen") { inclusive = true }
            }
        }
        else {
            navHostController.navigate("AccountScreen") {
                popUpTo("AccountScreen") { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ClothShare",
            style = TextStyle(
                fontSize = 48.sp,  // Kích thước chữ lớn
                fontWeight = FontWeight.Bold,  // Đậm
                color = MaterialTheme.colorScheme.primary,  // Màu sắc chữ từ theme
            )
        )
    }
}