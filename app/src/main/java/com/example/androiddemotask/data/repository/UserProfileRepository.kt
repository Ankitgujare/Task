package com.example.androiddemotask.data.repository

import com.example.androiddemotask.data.local.UserProfileDao
import com.example.androiddemotask.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.insertUserProfile(profile)
    }

    suspend fun updateProfileImage(imageUri: String) {
        val currentProfile = userProfileDao.getUserProfile().first()
        val updatedProfile = currentProfile?.copy(profileImageUri = imageUri) 
            ?: UserProfile(name = "User", profileImageUri = imageUri, location = null, latitude = null, longitude = null)
        userProfileDao.insertUserProfile(updatedProfile)
    }

    suspend fun updateLocation(location: String, latitude: Double?, longitude: Double?) {
        val currentProfile = userProfileDao.getUserProfile().first()
        val updatedProfile = currentProfile?.copy(
            location = location,
            latitude = latitude,
            longitude = longitude
        ) ?: UserProfile(name = "User", profileImageUri = null, location = location, latitude = latitude, longitude = longitude)
        userProfileDao.insertUserProfile(updatedProfile)
    }
}
