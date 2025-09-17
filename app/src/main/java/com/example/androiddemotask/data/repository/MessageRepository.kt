package com.example.androiddemotask.data.repository

import com.example.androiddemotask.data.local.MessageDao
import com.example.androiddemotask.data.model.Message
import com.example.androiddemotask.data.model.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getAllMessages(): Flow<List<Message>> = messageDao.getAllMessages()

    suspend fun sendMessage(
        content: String,
        messageType: MessageType = MessageType.TEXT,
        imageUri: String? = null,
        audioUri: String? = null
    ) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis(),
            isFromUser = true,
            messageType = messageType,
            imageUri = imageUri,
            audioUri = audioUri
        )
        messageDao.insertMessage(message)
    }

    suspend fun simulateReceivedMessage(
        content: String,
        messageType: MessageType = MessageType.TEXT,
        imageUri: String? = null,
        audioUri: String? = null
    ) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis(),
            isFromUser = false,
            messageType = messageType,
            imageUri = imageUri,
            audioUri = audioUri
        )
        messageDao.insertMessage(message)
    }

    suspend fun clearAllMessages() {
        messageDao.deleteAllMessages()
    }
}
