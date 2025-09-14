package com.example.androiddemotask.domain.service

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val geocoder = Geocoder(context, Locale.getDefault())

    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    // Build a readable address
                    val addressParts = mutableListOf<String>()
                    
                    address.locality?.let { addressParts.add(it) }
                    address.adminArea?.let { addressParts.add(it) }
                    address.countryName?.let { addressParts.add(it) }
                    
                    if (addressParts.isNotEmpty()) {
                        addressParts.joinToString(", ")
                    } else {
                        "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
                    }
                } else {
                    "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
                }
            } catch (e: Exception) {
                "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
            }
        }
    }
}
