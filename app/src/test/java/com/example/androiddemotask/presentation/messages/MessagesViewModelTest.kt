package com.example.androiddemotask.presentation.messages

import com.example.androiddemotask.data.model.Message
import com.example.androiddemotask.data.model.MessageType
import com.example.androiddemotask.data.repository.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MessagesViewModelTest {

    @Mock
    private lateinit var messageRepository: MessageRepository

    private lateinit var viewModel: MessagesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MessagesViewModel(messageRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage should call repository with correct parameters`() = runTest {
        // Given
        val content = "Test message"
        val messageType = MessageType.TEXT
        val imageUri = "test-image-uri"

        // When
        viewModel.sendMessage(content, messageType, imageUri)

        // Then
        verify(messageRepository).sendMessage(content, messageType, imageUri)
    }

    @Test
    fun `simulateReceivedMessage should call repository`() = runTest {
        // When
        viewModel.simulateReceivedMessage()

        // Then
        verify(messageRepository).simulateReceivedMessage(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any()
        )
    }

    @Test
    fun `clearMessages should call repository`() = runTest {
        // When
        viewModel.clearMessages()

        // Then
        verify(messageRepository).clearAllMessages()
    }

    @Test
    fun `loadMessages should update UI state with messages`() = runTest {
        // Given
        val mockMessages = listOf(
            Message(
                id = "1",
                content = "Test message",
                timestamp = System.currentTimeMillis(),
                isFromUser = true,
                messageType = MessageType.TEXT
            )
        )
        whenever(messageRepository.getAllMessages()).thenReturn(flowOf(mockMessages))

        // When
        viewModel = MessagesViewModel(messageRepository)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.messages == mockMessages)
    }
}
