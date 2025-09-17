package com.example.androiddemotask.presentation.profile

import android.net.Uri
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SDK-aware permission names
    val imagesPermission = if (Build.VERSION.SDK_INT >= 33) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Permission states
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(imagesPermission)
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    // Track if we've asked before to detect permanent denial (Don't allow selected multiple times)
    var askedCamera by remember { mutableStateOf(false) }
    var askedStorage by remember { mutableStateOf(false) }
    var askedLocation by remember { mutableStateOf(false) }

    // Dialog states
    var showCameraDeniedForever by remember { mutableStateOf(false) }
    var showStorageDeniedForever by remember { mutableStateOf(false) }
    var showLocationDeniedForever by remember { mutableStateOf(false) }

    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            android.net.Uri.fromParts("package", context.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // Creatd temporary file for camera capture
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launchers
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.updateProfileImage(imageUri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateProfileImage(it.toString())
        }
    }

    // Function to create image file for camera
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.cacheDir, "images")
        if (!storageDir.exists()) storageDir.mkdirs()
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    val userProfile = uiState.userProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            if (userProfile?.profileImageUri != null) {
                AsyncImage(
                    model = userProfile.profileImageUri,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Profile Image",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = uiState.userProfile?.name ?: "User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location Display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = userProfile?.location ?: "No location set",
                style = MaterialTheme.typography.bodyMedium,
                color = if (userProfile?.location != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Camera Button
            Button(
                onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        val imageFile = createImageFile()
                        imageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        cameraLauncher.launch(imageUri)
                    } else if (cameraPermissionState.status.shouldShowRationale) {
                        // User denied once: show rationale or directly request again
                        askedCamera = true
                        cameraPermissionState.launchPermissionRequest()
                    } else {
                        // Either first time or permanently denied
                        if (askedCamera) {
                            showCameraDeniedForever = true
                        } else {
                            askedCamera = true
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Photo")
            }

            // Gallery Button
            Button(
                onClick = {
                    if (storagePermissionState.status.isGranted) {
                        galleryLauncher.launch("image/*")
                    } else if (storagePermissionState.status.shouldShowRationale) {
                        askedStorage = true
                        storagePermissionState.launchPermissionRequest()
                    } else {
                        if (askedStorage) {
                            showStorageDeniedForever = true
                        } else {
                            askedStorage = true
                            storagePermissionState.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select from Gallery")
            }

            // Location Button
            Button(
                onClick = {
                    if (locationPermissionState.status.isGranted) {
                        viewModel.updateLocation(true)
                    } else if (locationPermissionState.status.shouldShowRationale) {
                        askedLocation = true
                        locationPermissionState.launchPermissionRequest()
                    } else {
                        if (askedLocation) {
                            showLocationDeniedForever = true
                        } else {
                            askedLocation = true
                            locationPermissionState.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (uiState.isLoading) "Getting Location..." else "Update Location")
            }
        }

        // Loading indicator
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Error message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Permanent denial dialogs with Settings fallback
    if (showCameraDeniedForever) {
        AlertDialog(
            onDismissRequest = { showCameraDeniedForever = false },
            confirmButton = {
                TextButton(onClick = {
                    showCameraDeniedForever = false
                    openAppSettings()
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showCameraDeniedForever = false }) { Text("Cancel") }
            },
            title = { Text("Camera permission required") },
            text = { Text("You have denied camera permission multiple times. Enable it from Settings to use the camera.") }
        )
    }
    if (showStorageDeniedForever) {
        AlertDialog(
            onDismissRequest = { showStorageDeniedForever = false },
            confirmButton = {
                TextButton(onClick = {
                    showStorageDeniedForever = false
                    openAppSettings()
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showStorageDeniedForever = false }) { Text("Cancel") }
            },
            title = { Text("Photos permission required") },
            text = { Text("You have denied photo library permission multiple times. Enable it from Settings to select images.") }
        )
    }
    if (showLocationDeniedForever) {
        AlertDialog(
            onDismissRequest = { showLocationDeniedForever = false },
            confirmButton = {
                TextButton(onClick = {
                    showLocationDeniedForever = false
                    openAppSettings()
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDeniedForever = false }) { Text("Cancel") }
            },
            title = { Text("Location permission required") },
            text = { Text("You have denied location permission multiple times. Enable it from Settings to update your location.") }
        )
    }
}