package com.example.androiddemotask.domain.usecase

import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(apiKey: String): Flow<List<NewsArticle>> {
        return newsRepository.getTopHeadlines(apiKey)
    }
}
