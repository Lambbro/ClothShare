package com.example.clothshare.ui.screens.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.clothshare.R
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingXLarge
import com.example.clothshare.ui.theme.spacingXXLarge
import com.example.clothshare.ui.theme.spacingXXXLarge

@Composable
fun AccountScreen(
    navHostController: NavHostController
) {
    val accountRepository = AccountRepository(navHostController)

    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("SignIn") }
    var signInError by remember { mutableStateOf("") }
    var signUpError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopImage(
            image = R.drawable.clothes,
            description = "Clothes",
            text = "ClothShare"
        )

        Spacer(modifier = Modifier.padding(top = spacingXXLarge))

        Card(
            modifier = Modifier
                .padding(spacingXLarge)
                .padding(bottom = spacingXXXLarge)
                .shadow(elevation = spacingMedium, shape = RoundedCornerShape(spacingLarge)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(spacingLarge)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ItemTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it }
                )
                Column {
                    if (selectedType == "SignIn") {
                        val inputEmail = remember { mutableStateOf("")}
                        val inputPassword = remember { mutableStateOf("")}
                        SignInBox(
                            email = inputEmail,
                            password = inputPassword,
                            buttonClick = {
                                val email = inputEmail.value
                                val password = inputPassword.value
                                accountRepository.login(email, password)
                            },
                            forgotPassword = {
                                Toast.makeText(context, "Just remember it", Toast.LENGTH_SHORT)
                                    .show()
                            },
                        )
                        if (signInError.isNotEmpty()) {
                            Text(
                                text = signInError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                    } else {
                        val inputEmail = remember { mutableStateOf("")}
                        val inputPassword = remember { mutableStateOf("")}
                        SignUpBox(
                            email = inputEmail,
                            password = inputPassword,
                            confirmPassword = remember { mutableStateOf("") },
                            buttonClick = {
                                val email = inputEmail.value
                                val password = inputPassword.value
                                accountRepository.register(email, password)
                            }
                        )
                        if (signUpError.isNotEmpty()) {
                            Text(
                                text = signUpError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}