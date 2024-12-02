package com.example.clothshare.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clothshare.R
import com.example.clothshare.ui.theme.fontXXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingSmall
import com.example.clothshare.ui.theme.spacingXLarge
import com.example.clothshare.ui.theme.spacingXXXLarge

@Composable
fun SignInTest() {
    var selectedType by remember { mutableStateOf("SignUp") }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopImage(
            image = R.drawable.clothes,
            description = "Clothes",
            text = "ClothShare"
        )

        Spacer(modifier = Modifier.padding(top = spacingXLarge))

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
                        SignInBox(
                            email = remember { mutableStateOf("") },
                            password = remember { mutableStateOf("") },
                            buttonClick = {},
                            forgotPassword = {}
                        )
                    } else {
                        SignUpBox(
                            email = remember { mutableStateOf("") },
                            password = remember { mutableStateOf("") },
                            confirmPassword = remember { mutableStateOf("") },
                            buttonClick = {}
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun TopImage(
    image: Int,
    description: String,
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomEnd = 150.dp, bottomStart = 150.dp))
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = description,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = text,
            color = Color.Black,
            fontSize = fontXXLarge,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(spacingLarge)
        )
    }
}

@Composable
fun ItemTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    Column {
        Row(modifier = Modifier.padding(vertical = spacingMedium)) {
            Button(
                onClick = { onTypeSelected("SignIn") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "SignIn") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(spacingLarge),
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(horizontal = spacingSmall)
                    .shadow(elevation = spacingSmall, shape = RoundedCornerShape(spacingLarge))
            ) {
                Text(
                    text = "Sign In",
                    color = if (selectedType == "SignIn") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
            Button(
                onClick = { onTypeSelected("SignUp") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "SignUp") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(spacingLarge),
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(horizontal = spacingSmall)
                    .shadow(elevation = spacingSmall, shape = RoundedCornerShape(spacingLarge))

            ) {
                Text(
                    text = "Sign Up",
                    color = if (selectedType == "SignUp") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    Column(
        modifier = Modifier
            .padding(vertical = spacingMedium)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(spacingLarge))
                .background(MaterialTheme.colorScheme.background),
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,

            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
    }
}

@Composable
fun SignInBox(
    email: MutableState<String>,
    password: MutableState<String>,
    buttonClick: () -> Unit,
    forgotPassword: () -> Unit
) {
    InputField(
        value = email.value,
        onValueChange = { email.value = it },
        label = "Email"
    )

    InputField(
        value = password.value,
        onValueChange = { password.value = it },
        label = "Password",
        isPassword = true
    )

    Spacer(modifier = Modifier.padding(top = spacingMedium))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = forgotPassword) {
            Text(text = "Forgot Password?")
        }
    }

    Spacer(modifier = Modifier.padding(top = spacingMedium))

    Button(
        onClick = buttonClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacingXXXLarge)
    ) {
        Text(text = "Sign In")
    }
}

@Composable
fun SignUpBox(
    email: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    buttonClick: () -> Unit
) {
    InputField(
        value = email.value,
        onValueChange = { email.value = it },
        label = "Email"
    )

    InputField(
        value = password.value,
        onValueChange = { password.value = it },
        label = "Password",
        isPassword = true
    )
    InputField(
        value = confirmPassword.value,
        onValueChange = { confirmPassword.value = it },
        label = "Confirm Password",
        isPassword = true
    )
    Spacer(modifier = Modifier.padding(top = spacingLarge))

    Button(
        onClick = buttonClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacingXXXLarge)
    ) {
        Text(text = "Sign Up")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TestPreview() {
    SignInTest()
}