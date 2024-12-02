package com.example.clothshare.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.clothshare.R
import com.example.clothshare.datas.models.Item
import com.example.clothshare.datas.repositories.ItemRepository
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingXLarge

@Composable
fun HomeScreen(navController: NavHostController) {
    val itemRepository = ItemRepository()
    var listItem by remember { mutableStateOf(emptyList<Item>()) }

    val isSearching = remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    itemRepository.getAllItem { items ->
        listItem = items
    }

    val filteredItems = listItem.filter { item ->
        item.name.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.authorEmail.contains(searchQuery, ignoreCase = true)
    }

    Column (
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .padding(spacingLarge),
    )  {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ClothShare",
                fontSize = fontXLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier
                    .height(spacingXLarge)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        isSearching.value = !isSearching.value
                    }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = spacingMedium))
        if (isSearching.value) {
            TextField(
                modifier = Modifier
                    .padding(bottom = spacingMedium)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(spacingLarge))
                    .background(MaterialTheme.colorScheme.background),
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                },
                label = { Text(text = "Searching for...") },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
        ListContent(items = filteredItems, navController = navController)
    }
}