package com.example.androiddemotask.data.repository

import com.example.androiddemotask.data.local.NewsDao
import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.data.model.NewsArticleDto
import com.example.androiddemotask.data.remote.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDao: NewsDao
) {
    fun getTopHeadlines(apiKey: String): Flow<List<NewsArticle>> = flow {
        try {
            val response = newsApiService.getTopHeadlines(apiKey = apiKey)
            val articles = response.articles.map { it.toNewsArticle() }
            newsDao.insertArticles(articles)
            emit(articles)
        } catch (e: Exception) {
            // Return cached data if API fails
            emit(newsDao.getAllArticles().first())
        }
    }

    fun searchNews(query: String, apiKey: String): Flow<List<NewsArticle>> = flow {
        try {
            val response = newsApiService.searchNews(query = query, apiKey = apiKey)
            val articles = response.articles.map { it.toNewsArticle() }
            emit(articles)
        } catch (e: Exception) {
            // Return cached search results if API fails
            emit(newsDao.searchArticles(query).first())
        }
    }

    fun getCachedArticles(): Flow<List<NewsArticle>> = newsDao.getAllArticles()

    fun searchCachedArticles(query: String): Flow<List<NewsArticle>> = newsDao.searchArticles(query)

    private fun NewsArticleDto.toNewsArticle(): NewsArticle {
        return NewsArticle(
            url = url,
            title = title,
            description = description,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            source = source.name,
            content = content,
            author = author
        )
    }
}
