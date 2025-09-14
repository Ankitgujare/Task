package com.example.androiddemotask.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddemotask.data.model.NewsArticle
import com.example.androiddemotask.domain.usecase.GetTopHeadlinesUseCase
import com.example.androiddemotask.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTopHeadlines()
    }

    fun loadTopHeadlines() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getTopHeadlinesUseCase("2167b224dbd3476e8346bfddebee4133").collect { articles ->
                    _uiState.value = _uiState.value.copy(
                        articles = articles,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun searchNews(query: String) {
        if (query.isBlank()) {
            loadTopHeadlines()
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                searchNewsUseCase(query, "YOUR_API_KEY_HERE").collect { articles ->
                    _uiState.value = _uiState.value.copy(
                        articles = articles,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        loadTopHeadlines()
    }

    fun pullToRefresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            try {
                getTopHeadlinesUseCase("2167b224dbd3476e8346bfddebee4133").collect { articles ->
                    _uiState.value = _uiState.value.copy(
                        articles = articles,
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

data class HomeUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
