package com.yeremi.ocupa2app.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ZoibeRepository
import com.yeremi.ocupa2app.network.models.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VideosUiState(
    val isLoading:
    Boolean = false,
    val videos:
    List<VideoItem> =
        emptyList(),
    val error:
    String? = null
)

class VideosViewModel(
    private val repository:
    ZoibeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            VideosUiState()
        )

    val uiState:
            StateFlow<VideosUiState> =
        _uiState

    init {
        loadVideos()
    }

    fun loadVideos() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            repository
                .getVideos()
                .onSuccess {

                    _uiState.value =
                        VideosUiState(
                            videos =
                                it
                        )
                }
                .onFailure {

                    _uiState.value =
                        VideosUiState(
                            error =
                                it.message
                                    ?: "No se pudieron cargar los videos."
                        )
                }
        }
    }
}