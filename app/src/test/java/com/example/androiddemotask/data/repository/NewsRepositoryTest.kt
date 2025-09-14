package com.example.androiddemotask.data.repository

import com.example.androiddemotask.data.local.NewsDao
import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.data.model.NewsArticleDto
import com.example.androiddemotask.data.model.NewsResponse
import com.example.androiddemotask.data.model.NewsSource
import com.example.androiddemotask.data.remote.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class NewsRepositoryTest {

    @Mock
    private lateinit var newsApiService: NewsApiService

    @Mock
    private lateinit var newsDao: NewsDao

    private lateinit var repository: NewsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = NewsRepository(newsApiService, newsDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTopHeadlines should return articles from API and cache them`() = runTest {
        // Given
        val apiKey = "test-api-key"
        val mockResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                NewsArticleDto(
                    source = NewsSource(id = "test", name = "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = "test-url",
                    urlToImage = "test-image",
                    publishedAt = "2023-01-01",
                    content = "Test Content"
                )
            )
        )
        whenever(newsApiService.getTopHeadlines(apiKey = apiKey)).thenReturn(mockResponse)

        // When
        val result = repository.getTopHeadlines(apiKey).first()

        // Then
        verify(newsApiService).getTopHeadlines(apiKey = apiKey)
        verify(newsDao).insertArticles(org.mockito.kotlin.any())
        assert(result.isNotEmpty())
        assert(result.first().title == "Test Title")
    }

    @Test
    fun `searchNews should return search results from API`() = runTest {
        // Given
        val query = "test query"
        val apiKey = "test-api-key"
        val mockResponse = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                NewsArticleDto(
                    source = NewsSource(id = "test", name = "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = "test-url",
                    urlToImage = "test-image",
                    publishedAt = "2023-01-01",
                    content = "Test Content"
                )
            )
        )
        whenever(newsApiService.searchNews(query = query, apiKey = apiKey)).thenReturn(mockResponse)

        // When
        val result = repository.searchNews(query, apiKey).first()

        // Then
        verify(newsApiService).searchNews(query = query, apiKey = apiKey)
        assert(result.isNotEmpty())
        assert(result.first().title == "Test Title")
    }
}
