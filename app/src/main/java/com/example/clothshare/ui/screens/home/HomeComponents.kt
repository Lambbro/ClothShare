package com.example.clothshare.ui.screens.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.clothshare.R
import com.example.clothshare.datas.models.Item
import com.example.clothshare.datas.models.Transaction
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.datas.repositories.TransactionRepository
import com.example.clothshare.datas.repositories.UserRepository
import com.example.clothshare.ui.theme.fontLarge
import com.example.clothshare.ui.theme.fontMedium
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingSmall
import com.example.clothshare.ui.theme.spacingXLarge
import java.util.Calendar

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Hitem(item: Item,onClick: ()-> Unit = {}, onFavoriteClick: () -> Unit = {}, isFavorite: Boolean = false) {
    var isFavorItem by remember { mutableStateOf(isFavorite) }

    Column(
        modifier = Modifier
            .padding(spacingMedium)
            .shadow(elevation = spacingSmall, shape = RoundedCornerShape(spacingMedium))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(spacingMedium)
            )
            .padding(spacingMedium)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = spacingSmall)
                .fillMaxWidth()
        ) {
            GlideImage(
                model = item.imageUrl[0],
                contentDescription = "Clothes",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(spacingSmall)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.clothes),
                failure = placeholder(R.drawable.error_image)
            )
            Image(
                painter = if (isFavorItem) painterResource(R.drawable.ic_favor) else painterResource(R.drawable.ic_unfavor),
                contentDescription = "Like",
                modifier = Modifier
                    .size(spacingXLarge)
                    .align(Alignment.TopEnd)
                    .offset(x = (-spacingMedium), y = spacingMedium)
                    .background(
                        color = Color.Gray.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .padding(spacingSmall)
                    .clickable {
                        isFavorItem = !isFavorItem
                        onFavoriteClick()
                    },
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.name,
                fontSize = fontLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = item.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListContent (items: List<Item>, isFavoriteList: Boolean = false, navController: NavHostController) {
    var inx by remember { mutableIntStateOf(-1) }
    var isList by remember { mutableStateOf(true) }

    val userRepository = UserRepository()
    val accountRepository = AccountRepository(navController)
    val transactionRepository = TransactionRepository()

    val currentUserEmail = accountRepository.getCurrentUserEmail()

    var showDialog by remember { mutableStateOf(false) }

    var yy by remember { mutableStateOf("") }
    var mm by remember { mutableStateOf("") }
    var dd by remember { mutableStateOf("") }
    var hh by remember { mutableStateOf("") }
    var mn by remember { mutableStateOf("") }

    Column{
        if (isList) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 cột
                contentPadding = PaddingValues(spacingMedium), // padding lưới
                horizontalArrangement = Arrangement.spacedBy(spacingSmall), // khoảng cách giữa các cột
                verticalArrangement = Arrangement.spacedBy(spacingSmall) // khoảng cách giữa các hàng
            ) {
                items(items.size) { index ->
                    if (!isFavoriteList) {
                        Hitem(items[index], onClick = {
                            inx = index
                            isList = false
                        }, onFavoriteClick = {
                            val email = accountRepository.getCurrentUserEmail()
                            userRepository.addToFavoriteList(email, items[index]) { success ->
                                if (success) {
                                    Log.d("ListContent", "Thêm thành công")
                                } else {
                                    Log.d("ListContent", "Thêm thất bại")
                                }
                            }
                        })
                    } else {
                        Hitem(items[index], onClick = {
                            inx = index
                            isList = false
                        }, onFavoriteClick = {
                            userRepository.removeFromFavoriteList(currentUserEmail, items[index]) { success ->
                                if (success) {
                                    Log.d("ListContent", "Xóa thành công")
                                } else {
                                    Log.d("ListContent", "Xóa thất bại")
                                }
                            }
                        }, isFavorite = true)
                    }
                }
            }
        } else {
            Row (
                modifier = Modifier
                    .padding(bottom = spacingMedium)
                    .clickable {
                    isList = true
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_left_arrow),
                    contentDescription = "Back",
                    Modifier.width(spacingLarge).padding(end = spacingSmall)
                )
                Text(text="Back to list")
            }
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(items[inx].imageUrl.size) { index ->
                    GlideImage(
                        model = items[inx].imageUrl[index],
                        contentDescription = "Image",
                        modifier = Modifier
                            .height(250.dp)
                            .width(350.dp)
                            .padding(end = spacingSmall),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Text(
                text = items[inx].name,
                fontSize = fontXLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = spacingSmall)
            )
            Text(
                text = "Sets: ${items[inx].number}",
                fontSize = fontMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = spacingSmall)
            )
            Text(
                text = "Location: ${items[inx].location}",
                fontSize = fontMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = spacingSmall)
            )
            Text(
                text = "Sharer: ${items[inx].authorEmail}",
                fontSize = fontMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = items[inx].description,
                fontSize = fontMedium,
                modifier = Modifier.padding(vertical = spacingSmall)
            )
            Button(onClick = {
                showDialog = true
                val calendar = Calendar.getInstance()
                hh = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                mn = String.format("%02d", calendar.get(Calendar.MINUTE))
                yy = calendar.get(Calendar.YEAR).toString()
                mm = (calendar.get(Calendar.MONTH) + 1).toString()
                dd = calendar.get(Calendar.DAY_OF_MONTH).toString()
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacingLarge),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Receive this item",
                    style = TextStyle(
                        fontSize = fontMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            if (showDialog) {
                var quantities by remember { mutableStateOf("") }
                Dialog(
                    onDismissRequest = { showDialog = false },
                    content = {
                        Column(
                            modifier = Modifier
                                .background(color = Color.White)
                                .border(1.dp, Color.Gray, RoundedCornerShape(spacingLarge))
                                .padding(spacingLarge)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Pick Up Time",
                                style = TextStyle(
                                    fontSize = fontLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$hh:$mn")
                                Spacer (modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.clock),
                                    contentDescription = "Pick up time",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                        val calendar = Calendar.getInstance()
                                        TimePickerDialog(
                                            navController.context,
                                            { _, hour, minute ->
                                                hh = String.format("%02d", hour)
                                                mn = String.format("%02d", minute)
                                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                                calendar.set(Calendar.MINUTE, minute)
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    }
                                )
                            }
                            Text(
                                text = "Pick Up Date",
                                style = TextStyle(
                                    fontSize = fontLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$dd/$mm/$yy")
                                Spacer (modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Pick up time",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                        val calendar = Calendar.getInstance()
                                        DatePickerDialog(
                                            navController.context,
                                            { _, year, month, dayOfMonth ->
                                                yy = year.toString()
                                                mm = (month + 1).toString()
                                                dd = dayOfMonth.toString()
                                                calendar.set(Calendar.YEAR, year)
                                                calendar.set(Calendar.MONTH, month)
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                                )
                            }

                            Row (modifier = Modifier.fillMaxWidth().padding(vertical = spacingMedium)) {
                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(spacingLarge))
                                        .background(MaterialTheme.colorScheme.background),
                                    value = quantities,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() }) {
                                            quantities = it
                                        }
                                    },
                                    label = { Text(text = "Quantities") },

                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    )
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(onClick = { showDialog = false }) {
                                    Text(text = "No")
                                }
                                Button(onClick = {
                                    val calendar = Calendar.getInstance()
                                    val currentTime = System.currentTimeMillis()

                                    calendar.set(Calendar.HOUR_OF_DAY, hh.toInt())
                                    calendar.set(Calendar.MINUTE, mn.toInt())
                                    calendar.set(Calendar.YEAR, yy.toInt())
                                    calendar.set(Calendar.MONTH, mm.toInt())
                                    calendar.set(Calendar.DAY_OF_MONTH, dd.toInt())
                                    val time = calendar.timeInMillis

                                    val transaction = Transaction(
                                        itemID = items[inx].itemID,
                                        requesterEmail = accountRepository.getCurrentUserEmail(),
                                        receiverEmail = items[inx].authorEmail,
                                        pickUpTime = time,
                                        requestTime = currentTime,
                                        quantities = quantities.toInt()
                                    )

                                    transactionRepository.createTransaction(transaction) { success ->
                                        if (success) {
                                            Log.d("ListContent", "Thêm thành công")
                                        } else {
                                            Log.d("ListContent", "Thêm thất bại")
                                        }
                                    }
                                    showDialog = false
                                }) {
                                    Text(text = "Yes")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}