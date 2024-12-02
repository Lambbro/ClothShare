package com.example.clothshare

import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clothshare.datas.FirebaseSingleton
import com.example.clothshare.ui.screens.account.AccountScreen
import com.example.clothshare.ui.screens.main.MainScreen
import com.example.clothshare.ui.screens.main.SplashScreen
import com.example.clothshare.ui.theme.ClothShareTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        val auth = FirebaseSingleton.auth
        setContent {
            ClothShareTheme {
                val navHostController = rememberNavController()
                Navigation(
                    navHostController = navHostController,
                    auth = auth
                )
            }
        }
    }
}
@Composable
fun Navigation(
    navHostController: NavHostController,
    auth:FirebaseAuth
){
    NavHost(
        navController = navHostController,
        startDestination = "SplashScreen"
    ){
        composable("SplashScreen")
        {
            SplashScreen(auth = auth, navHostController = navHostController)
        }
        composable("AccountScreen")
        {
            AccountScreen(navHostController = navHostController)
        }
        composable("MainScreen")
        {
            MainScreen(navHostController = navHostController)
        }
    }
}
