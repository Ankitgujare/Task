package com.example.androiddemotask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val source: String,
    val content: String?,
    val author: String?
)
