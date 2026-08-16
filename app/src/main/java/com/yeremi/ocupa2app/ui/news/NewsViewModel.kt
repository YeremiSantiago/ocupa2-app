package com.yeremi.ocupa2app.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.NewsRepository
import com.yeremi.ocupa2app.data.repository.NewsResult
import com.yeremi.ocupa2app.network.models.NewsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NewsUiState(
    val isLoading: Boolean = false,
    val news: List<NewsItem> = emptyList(),
    val selectedNews: NewsItem? = null,
    val errorMessage: String? = null
)

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState

    fun loadNews() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getNews()) {
                is NewsResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, news = result.data)
                is NewsResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    // No existe /news/{id} — seleccionamos por índice dentro de la lista que ya tenemos en memoria.
    fun selectNews(index: Int) {
        val item = _uiState.value.news.getOrNull(index)
        _uiState.value = _uiState.value.copy(selectedNews = item)
    }
}