package com.example.androiddemotask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val profileImageUri: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?
)
