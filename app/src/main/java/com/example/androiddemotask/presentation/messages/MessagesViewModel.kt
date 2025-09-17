package com.example.androiddemotask.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddemotask.data.model.Message
import com.example.androiddemotask.data.model.MessageType
import com.example.androiddemotask.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            messageRepository.getAllMessages().collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun sendMessage(
        content: String,
        messageType: MessageType = MessageType.TEXT,
        imageUri: String? = null,
        audioUri: String? = null
    ) {
        if (content.isBlank() && imageUri == null && audioUri == null) return
        
        viewModelScope.launch {
            messageRepository.sendMessage(content, messageType, imageUri, audioUri)
        }
    }

    fun simulateReceivedMessage() {
        viewModelScope.launch {
            val responses = listOf(
                "Hello! How are you?",
                "That's interesting!",
                "I see what you mean.",
                "Thanks for sharing!",
                "Great to hear from you!",
                "What do you think about this?",
                "I agree with you.",
                "Let me know what you think."
            )
            val randomResponse = responses.random()
            messageRepository.simulateReceivedMessage(randomResponse)
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            messageRepository.clearAllMessages()
        }
    }
}

data class MessagesUiState(
    val messages: List<Message> = emptyList()
)
