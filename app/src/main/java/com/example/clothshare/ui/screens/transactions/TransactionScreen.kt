package com.example.clothshare.ui.screens.transactions

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import com.example.clothshare.datas.models.Transaction
import com.example.clothshare.datas.models.TransactionStatus
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.datas.repositories.TransactionRepository
import com.example.clothshare.ui.theme.AcceptButton
import com.example.clothshare.ui.theme.RejectButton
import com.example.clothshare.ui.theme.fontLarge
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingSmall
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionScreen (navController: NavHostController) {
    var myRequest by remember { mutableStateOf(true) }

    val accountRepository = AccountRepository(navController)
    val transactionRepository = TransactionRepository()

    val currentUserEmail = accountRepository.getCurrentUserEmail()

    var listTransaction by remember { mutableStateOf<List<Transaction>?>(null) }

    if (myRequest) {
        transactionRepository.getAllByRequester(currentUserEmail) { transactions ->
            listTransaction = transactions
        }
    } else {
        transactionRepository.getAllByReceiver(currentUserEmail) { transactions ->
            listTransaction = transactions
        }
    }

    Column (
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
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
                text = "My Request",
                fontSize = fontLarge,
                color = if (myRequest) MaterialTheme.colorScheme.primary else Color.Black,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .weight(1f)
                    .clickable {
                        myRequest = true
                    },
                textAlign = TextAlign.Center
            )
            Text(
                text = "Other Request",
                fontSize = fontLarge,
                color = if (!myRequest) MaterialTheme.colorScheme.primary else Color.Black,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .weight(1f)
                    .clickable {
                        myRequest = false
                    },
                textAlign = TextAlign.Center
            )
        }
        if (listTransaction == null) {
            // Đang tải dữ liệu
            Text(text = "Đang tải dữ liệu...", modifier = Modifier.padding(spacingLarge))
        } else if (listTransaction!!.isEmpty()) {
            // Danh sách rỗng
            Text(
                text = "Danh sách rỗng",
                modifier = Modifier.padding(spacingLarge)
            )
        }
        else {
            if (myRequest) {
                ListMyTransaction(listTransaction!!)
            } else {
                ListOtherTransaction(listTransaction!!,
                    onUpdateList = { updateList ->
                        listTransaction = updateList
                    }
                )
            }
        }
    }
}

@Composable
fun ListMyTransaction(items: List<Transaction>) {
    LazyColumn {
        items(items.size) { index ->
            MyTransactionItem(items[index])
        }
    }
}

@Composable
fun ListOtherTransaction(items: List<Transaction>,onUpdateList: (List<Transaction>) -> Unit) {
    val transactionRepository = TransactionRepository()
    var updatedItems by remember { mutableStateOf(items) }
    LazyColumn {
        items(items.size) { index ->
            OtherTransactionItem(items[index],
                onReject = {
                    transactionRepository.changeStatus(items[index].transactionID, TransactionStatus.REJECTED) { success ->
                        if (success) {
                            Log.d("TransactionScreen", "Transaction rejected successfully")
                            updatedItems = updatedItems.toMutableList().apply {
                                this[index] = updatedItems[index].copy(status = TransactionStatus.REJECTED)
                            }
                            onUpdateList(updatedItems)
                        } else {
                            Log.d("TransactionScreen", "Failed to delete transaction")
                        }
                    }
                },
                onAccept = {
                    transactionRepository.changeStatus(items[index].transactionID, TransactionStatus.ACCEPTED) { success ->
                        if (success) {
                            Log.d("TransactionScreen", "Transaction accepted successfully")
                            updatedItems = updatedItems.toMutableList().apply {
                                this[index] = updatedItems[index].copy(status = TransactionStatus.ACCEPTED)
                            }
                            onUpdateList(updatedItems)
                        } else {
                            Log.d("TransactionScreen", "Failed to delete transaction")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun MyTransactionItem(item: Transaction) {
    val backgroundColor = when (item.status.toString()) {
        "REJECTED" -> Color.Red.copy(alpha = 0.1f)  // Màu đỏ nhạt cho trạng thái REJECTED
        "PENDING" -> Color.Yellow.copy(alpha = 0.1f)  // Màu vàng nhạt cho trạng thái PENDING
        "ACCEPTED" -> Color.Green.copy(alpha = 0.1f)  // Màu xanh nhạt cho trạng thái APPROVED
        else -> Color.Blue.copy(alpha = 0.1f)  // Màu xám cho các trạng thái khác
    }
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacingMedium, horizontal = spacingLarge),
        colors = CardColors(
            containerColor = backgroundColor,
            contentColor = Color.Black,
            disabledContainerColor = backgroundColor,
            disabledContentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(spacingLarge)) {
            Text(
                text = "To: ${item.receiverEmail}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(spacingSmall)) // Khoảng cách giữa các Text
            Text(
                text = "Status: ${item.status}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Quantities: ${item.quantities}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall)) // Khoảng cách giữa các Text
            Text(
                text = "Request from: ${millisToTime(item.requestTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall)) // Khoảng cách giữa các Text
            Text(
                text = "Pick up at: ${millisToTime(item.pickUpTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun OtherTransactionItem(item: Transaction, onReject: () -> Unit, onAccept: () -> Unit) {
    val backgroundColor = when (item.status.toString()) {
        "REJECTED" -> Color.Red.copy(alpha = 0.1f)  // Màu đỏ nhạt cho trạng thái REJECTED
        "PENDING" -> Color.Yellow.copy(alpha = 0.1f)  // Màu vàng nhạt cho trạng thái PENDING
        "ACCEPTED" -> Color.Green.copy(alpha = 0.1f)  // Màu xanh nhạt cho trạng thái APPROVED
        else -> Color.Blue.copy(alpha = 0.1f)  // Màu xám cho các trạng thái khác
    }
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacingMedium, horizontal = spacingLarge),
        colors = CardColors(
            containerColor = backgroundColor,
            contentColor = Color.Black,
            disabledContainerColor = backgroundColor,
            disabledContentColor = Color.Black
        )
    ) {
        Column (modifier = Modifier.padding(spacingLarge)) {
            Text(
                text = "From: ${item.requesterEmail}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Status: ${item.status}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Quantities: ${item.quantities}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Request from: ${millisToTime(item.requestTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingSmall))
            Text(
                text = "Pick up at: ${millisToTime(item.pickUpTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(spacingMedium))

            if (item.status == TransactionStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onReject() },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = RejectButton
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Reject")
                    }
                    Spacer(modifier = Modifier.width(spacingLarge))
                    Button(
                        onClick = { onAccept() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AcceptButton,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Accept")
                    }
                }
            }
        }
    }
}

fun millisToTime(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(millis))
}