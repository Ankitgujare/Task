package com.example.androiddemotask.presentation.profile

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddemotask.data.model.UserProfile
import com.example.androiddemotask.data.repository.UserProfileRepository
import com.example.androiddemotask.domain.service.LocationService
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val locationService: LocationService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userProfileRepository.getUserProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    userProfile = profile,
                    isLoading = false
                )
            }
        }
    }

    fun updateProfileImage(imageUri: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                userProfileRepository.updateProfileImage(imageUri)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile image"
                )
            }
        }
    }

    fun updateLocation(hasLocationPermission: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val location = getCurrentLocation(hasLocationPermission)
                if (location != null) {
                    // Get readable address from coordinates
                    val address = locationService.getAddressFromCoordinates(
                        location.latitude,
                        location.longitude
                    )
                    
                    val locationString = address ?: "Lat: ${String.format("%.4f", location.latitude)}, Lng: ${String.format("%.4f", location.longitude)}"
                    
                    userProfileRepository.updateLocation(
                        locationString,
                        location.latitude,
                        location.longitude
                    )
                    
                    // Reload profile to show updated location
                    loadUserProfile()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Unable to get current location. Please check your location settings."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to get location"
                )
            }
        }
    }

    private suspend fun getCurrentLocation(hasLocationPermission: Boolean): Location? {
        if (!hasLocationPermission) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                // First try to get last known location
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        continuation.resume(task.result)
                    } else {
                        // If last location is not available, request current location
                        val currentLocationRequest = CurrentLocationRequest.Builder()
                            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                            .setMaxUpdateAgeMillis(5000L)
                            .build()

                        fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
                            .addOnCompleteListener { currentLocationTask ->
                                if (currentLocationTask.isSuccessful) {
                                    continuation.resume(currentLocationTask.result)
                                } else {
                                    continuation.resume(null)
                                }
                            }
                    }
                }
            } catch (securityException: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)