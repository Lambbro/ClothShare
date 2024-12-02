package com.example.clothshare.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clothshare.datas.models.NavItem
import com.example.clothshare.ui.screens.add.AddScreen
import com.example.clothshare.ui.screens.home.FavoriteScreen
import com.example.clothshare.ui.screens.home.HomeScreen
import com.example.clothshare.ui.screens.profile.ProfileScreen
import com.example.clothshare.ui.screens.transactions.TransactionScreen

@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Favorite", Icons.Default.Star),
        NavItem("Add", Icons.Default.AddCircle),
        NavItem("Request", Icons.Default.Notifications),
        NavItem("Profile", Icons.Default.AccountCircle)
    )
    val navController = rememberNavController()
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column {
        Scaffold (
            bottomBar = {
                BottomNavBar(
                    navItemList = navItemList,
                    selectedIndex = selectedIndex,
                    onItemSelected = {
                        selectedIndex = it
                    }
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "HomeScreen",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 74.dp)
            ) {
                composable("HomeScreen") {
                    ContentScreen(
                        modifier = Modifier
                            .padding(innerPadding),
                        selectedItem = selectedIndex,
                        navHostController = navHostController
                    )
                }
            }
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier,
    selectedItem: Int = 0,
    navHostController: NavHostController
) {
    Column (modifier = Modifier.fillMaxSize()) {
        when (selectedItem) {
            0 -> HomeScreen(navController = navHostController)
            1 -> FavoriteScreen(navController = navHostController)
            2 -> AddScreen(navController = navHostController)
            3 -> TransactionScreen(navController = navHostController)
            4 -> ProfileScreen(navController = navHostController)
        }
    }
}