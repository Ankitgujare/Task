package com.example.androiddemotask.presentation.home

import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.domain.usecase.GetTopHeadlinesUseCase
import com.example.androiddemotask.domain.usecase.SearchNewsUseCase
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
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @Mock
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Mock
    private lateinit var searchNewsUseCase: SearchNewsUseCase

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(getTopHeadlinesUseCase, searchNewsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTopHeadlines should update UI state with articles`() = runTest {
        // Given
        val mockArticles = listOf(
            NewsArticle(
                url = "test-url",
                title = "Test Title",
                description = "Test Description",
                urlToImage = "test-image",
                publishedAt = "2023-01-01",
                source = "Test Source",
                content = "Test Content",
                author = "Test Author"
            )
        )
        whenever(getTopHeadlinesUseCase("YOUR_API_KEY_HERE")).thenReturn(flowOf(mockArticles))

        // When
        viewModel.loadTopHeadlines()

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.articles == mockArticles)
        assert(!viewModel.uiState.value.isLoading)
        assert(viewModel.uiState.value.error == null)
    }

    @Test
    fun `searchNews should update UI state with search results`() = runTest {
        // Given
        val query = "test query"
        val mockArticles = listOf(
            NewsArticle(
                url = "test-url",
                title = "Test Title",
                description = "Test Description",
                urlToImage = "test-image",
                publishedAt = "2023-01-01",
                source = "Test Source",
                content = "Test Content",
                author = "Test Author"
            )
        )
        whenever(searchNewsUseCase(query, "YOUR_API_KEY_HERE")).thenReturn(flowOf(mockArticles))

        // When
        viewModel.searchNews(query)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.articles == mockArticles)
        assert(!viewModel.uiState.value.isLoading)
        assert(viewModel.uiState.value.error == null)
    }

    @Test
    fun `updateSearchQuery should update search query state`() {
        // Given
        val query = "test query"

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assert(viewModel.searchQuery.value == query)
    }
}
