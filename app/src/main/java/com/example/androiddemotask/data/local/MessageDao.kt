package com.example.androiddemotask.data.local

import androidx.room.*
import com.example.androiddemotask.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getMessageCount(): Int
}
