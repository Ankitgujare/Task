package com.example.androiddemotask.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androiddemotask.data.repository.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NewsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Sync news data in background
            newsRepository.getTopHeadlines("YOUR_API_KEY_HERE").collect { }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
