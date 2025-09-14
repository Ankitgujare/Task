package com.example.androiddemotask.domain.usecase

import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(query: String, apiKey: String): Flow<List<NewsArticle>> {
        return newsRepository.searchNews(query, apiKey)
    }
}
