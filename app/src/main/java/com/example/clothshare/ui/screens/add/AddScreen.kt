package com.example.clothshare.ui.screens.add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.clothshare.R
import com.example.clothshare.datas.models.Item
import com.example.clothshare.datas.repositories.AccountRepository
import com.example.clothshare.datas.repositories.ItemRepository
import com.example.clothshare.ui.theme.fontXLarge
import com.example.clothshare.ui.theme.spacingLarge
import com.example.clothshare.ui.theme.spacingMedium
import com.example.clothshare.ui.theme.spacingXXLarge
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

@Composable
fun AddScreen(navController: NavHostController){
    val itemRepository = ItemRepository()
    val accountRepository = AccountRepository(navController)

    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var available by remember { mutableStateOf(true) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentUserEmail = accountRepository.getCurrentUserEmail()

    var showMapScreen by remember { mutableStateOf(false) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val geocoder = Geocoder(context, Locale.getDefault())

    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.size <= 5) {
            imageUris = uris
        } else {
            Toast.makeText(context, "You can upload up to 5 images", Toast.LENGTH_SHORT).show()
        }
    }
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { locationResult: Location? ->
            locationResult?.let {
                if (currentLocation == null) {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }
    Column (
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxWidth()
            .padding(spacingLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Item",
            modifier = Modifier
                .align(Alignment.Start),
            fontSize = fontXLarge,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacingMedium))
        InputField (
            value = name,
            onValueChange = { name = it },
            label = "Name"
        )
        InputField (
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )
        InputField (
            value = number,
            onValueChange = { number = it },
            label = "Number"
        )
        Box {
            InputField (
                value = location,
                onValueChange = { location = it },
                label = "Location"
            )
            Image(
                painter = painterResource(id = R.drawable.marker),
                contentDescription = "Map",
                modifier = Modifier
                    .size(spacingXXLarge)
                    .align(Alignment.CenterEnd)
                    .padding(end = spacingMedium)
                    .clickable { showMapScreen = !showMapScreen }
            )
        }

        if (showMapScreen) {

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(0.0, 0.0), 15f)
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                onMapClick = { latLng ->
                    try {
                        location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.get(0)?.getAddressLine(0) ?: ""
                        currentLocation = latLng

                    } catch (e: Exception) {
                        throw e
                    }
                },
                cameraPositionState = cameraPositionState
            ) {
                currentLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Current Location"
                    )
                }
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = spacingMedium)
            )
        }

        Text(text = "Select Images (Up to 5)", style = MaterialTheme.typography.bodyMedium)
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(imageUris.size) { index ->
                AsyncImage(
                    model = imageUris[index],
                    contentDescription = "Chosen Image",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Button to select images
        Button(onClick = { pickImages.launch("image/*") }) {
            Text("Select Images")
        }
        Spacer(modifier = Modifier.height(spacingLarge))
        Button(onClick = {
            if (name.isEmpty() || description.isEmpty() || number.isEmpty() || location.isEmpty()) {
                errorMessage = "Please fill all fields"
            } else if (number.toIntOrNull() == null || number.toInt() <= 0) {
                errorMessage = "Number must be a valid positive integer"
            } else {
                errorMessage = null

                itemRepository.uploadImages(imageUris) { imageUrls ->
                    val item = Item(
                        name = name,
                        description = description,
                        number = number.toInt(),
                        authorEmail = currentUserEmail,
                        location = location,
                        available = available,
                        imageUrl = imageUrls
                    )
                    itemRepository.createItem(item) { success ->
                        if (success) {
                            Toast.makeText(context, "Item added successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("MainScreen")
                        } else {
                            Toast.makeText(context, "Failed to add item.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }) {
            Text("Add Item")
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
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

            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
    }
}
