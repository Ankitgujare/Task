package com.example.androiddemotask.data.local

import androidx.room.*
import com.example.androiddemotask.data.model.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchArticles(query: String): Flow<List<NewsArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticle>)

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllArticles()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticleCount(): Int
}
