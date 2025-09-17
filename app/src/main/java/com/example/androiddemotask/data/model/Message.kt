package com.example.androiddemotask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val content: String,
    val timestamp: Long,
    val isFromUser: Boolean,
    val messageType: MessageType,
    val imageUri: String? = null,
    val audioUri: String? = null
)

enum class MessageType {
    TEXT, IMAGE, AUDIO
}
