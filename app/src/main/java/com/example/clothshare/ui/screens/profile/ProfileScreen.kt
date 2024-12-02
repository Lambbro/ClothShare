package com.example.clothshare.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clothshare.R
import com.example.clothshare.datas.models.User
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.datas.repositories.UserRepository
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingSmall


@Composable
fun ProfileScreen(navController: NavHostController) {
    val accountRepository = AccountRepository(navController = navController)
    val userRepository = UserRepository()

    val currentUserEmail = accountRepository.getCurrentUserEmail()

    val profile = remember { mutableStateOf<User?>(null) }
    val errorState = remember { mutableStateOf<String?>(null)}

    val isNewUser = remember { mutableStateOf(false) }

    val loadingState = remember { mutableStateOf(true) }
    LaunchedEffect (currentUserEmail) {
        if (currentUserEmail.isNotEmpty()) {
            userRepository.getUserByEmail(currentUserEmail) { user ->
                if (user != null) {
                    profile.value = user
                    loadingState.value = false
                } else {
                    errorState.value = "User not found"
                    loadingState.value = false
                }
            }
        } else {
            loadingState.value = false //if there is no email, stop loading
        }
    }
    Column (
        modifier = Modifier
            .safeDrawingPadding()
            .padding(spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Item",
            modifier = Modifier
                .align(Alignment.Start),
            fontSize = fontXLarge,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacingMedium))
        if (loadingState.value) {
            Text(text = "Loading...")
        } else {
            errorState.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (currentUserEmail.isNotEmpty() && profile.value != null) {
                Profile(user = profile.value!!)
                Button(
                    onClick = {
                        accountRepository.signOut()
                    },
                    modifier = Modifier.padding(top = spacingLarge)
                ) {
                    Text(text = "Sign Out")
                }
            } else {
                isNewUser.value = true
            }
        }
        if (isNewUser.value) {
            errorState.value = ""
            NewUser(userRepository = userRepository, email = currentUserEmail, onUserCreated = {
                isNewUser.value = false
                profile.value = it
            })
            Button(
                onClick = {
                    accountRepository.signOut()
                },
                modifier = Modifier.padding(top = spacingLarge)
            ) {
                Text(text = "Sign Out")
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Profile(user: User) {
    Column(
        modifier = Modifier.padding(spacingLarge).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            model = user.avatarUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(spacingLarge))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacingLarge)
                .shadow(elevation = spacingSmall, shape = RoundedCornerShape(spacingLarge)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa các hàng
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    maxLines = 1
                )
                Text(
                    text = "Email: ${user.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Phone: ${user.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun NewUser(userRepository: UserRepository, email: String, onUserCreated: (User) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val errorState = remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacingLarge),
        verticalArrangement = Arrangement.spacedBy(spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (avatarUri == null) {
            Image(
                painter = painterResource(id = R.drawable.ic_avatar),
                contentDescription = "Select Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable {launcher.launch("image/*")}
                    .padding(spacingMedium),
            )
        } else {
            avatarUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                )
            }
        }

        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacingMedium)
        ) {
            Text(
                text = "Enter your name:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Enter your phone number:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = {
                if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    if (avatarUri == null) {
                        errorState.value = "Please choose an avatar"
                    }
                    avatarUri?.let { uri ->
                        userRepository.saveUserAvatar(uri) { success, url ->
                            if (success) {
                                val newUser = User(
                                    name = name,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    avatarUrl = url
                                )
                                userRepository.createUser(newUser) { success ->
                                    if (success) {
                                        onUserCreated(newUser)
                                    } else {
                                        Log.e("NewUser", "Failed to create user")
                                        errorState.value = "Failed to create user. Please try again."
                                    }
                                }
                            } else {
                                Log.e("NewUser", "Failed to save avatar")
                                errorState.value = "Failed to save avatar. Please try again."
                            }
                        }
                    }
                } else {
                    Log.e("NewUser", "Invalid input")
                    errorState.value = "Please fill in all fields."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            errorState.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = spacingMedium)
                )
            }
        }
    }
}

@Composable
fun EditProfile() {

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PPrev() {
}