package com.example.clothshare.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.clothshare.datas.models.Item
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.datas.repositories.ItemRepository
import com.example.clothshare.datas.repositories.UserRepository
import com.example.clothshare.ui.theme.fontLarge
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium

@Composable
fun FavoriteScreen(navController: NavHostController) {
    val itemRepository = ItemRepository()
    val accountRepository = AccountRepository(navController)
    val userRepository = UserRepository()

    var listItem by remember { mutableStateOf<List<Item>?>(null) }

    var isFavorList by remember { mutableStateOf(false) }

    val currentUserEmail = accountRepository.getCurrentUserEmail()

    if (!isFavorList) {
        itemRepository.getAllItemOfUser(currentUserEmail) { items ->
            listItem = items
        }
    } else {
        userRepository.getFavorList(email = currentUserEmail) { items ->
            listItem = items
        }
    }
    Column (
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(spacingLarge),
    )  {
        Text(
            text = "ClothShare",
            modifier = Modifier
                .align(Alignment.Start),
            fontSize = fontXLarge,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacingMedium))
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Favorites",
                fontSize = fontLarge,
                color = if (isFavorList) MaterialTheme.colorScheme.primary else Color.Black,
                modifier = Modifier.fillMaxWidth(0.5f)
                    .weight(1f)
                    .clickable {
                        isFavorList = true
                    },
                textAlign = TextAlign.Center
            )
            Text(
                text = "Shared",
                fontSize = fontLarge,
                color = if (!isFavorList) MaterialTheme.colorScheme.primary else Color.Black,
                modifier = Modifier.fillMaxWidth(0.5f)
                    .weight(1f)
                    .clickable {
                        isFavorList = false
                    },
                textAlign = TextAlign.Center
            )
        }

        if (listItem == null) {
            // Đang tải dữ liệu
            Text(text = "Đang tải dữ liệu...", modifier = Modifier.padding(spacingLarge))
        } else if (listItem!!.isEmpty()) {
            // Danh sách rỗng
            Text(
                text = "Danh sách rỗng",
                modifier = Modifier.padding(spacingLarge)
            )
        } else {
            if (isFavorList) {
                ListContent(items = listItem!!, navController = navController, isFavoriteList = true)
            } else {
                ListContent(items = listItem!!, navController = navController)
            }
        }
    }
}