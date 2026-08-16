package com.yeremi.ocupa2app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ZoibeRepository
import com.yeremi.ocupa2app.network.models.Experience
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExperiencesUiState(
    val isLoading:
    Boolean = false,
    val isSaving:
    Boolean = false,
    val experiences:
    List<Experience> =
        emptyList(),
    val error:
    String? = null,
    val message:
    String? = null
)

class ExperiencesViewModel(
    private val repository:
    ZoibeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ExperiencesUiState()
        )

    val uiState:
            StateFlow<ExperiencesUiState> =
        _uiState

    init {
        loadExperiences()
    }

    fun loadExperiences() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            repository
                .getExperiences()
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            experiences = it,
                            error = null
                        )
                }
                .onFailure {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error =
                                it.message
                                    ?: "No se pudieron cargar las experiencias."
                        )
                }
        }
    }

    fun addExperience(
        title: String,
        description: String,
        certificateBase64:
        String?
    ) {

        if (
            title.isBlank() ||
            description.isBlank()
        ) {

            _uiState.value =
                _uiState.value.copy(
                    message =
                        "Completa el título y la descripción."
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSaving = true,
                    message = null
                )

            repository
                .createExperience(
                    title =
                        title,
                    description =
                        description,
                    certificateBase64 =
                        certificateBase64
                )
                .onSuccess { experience ->

                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            experiences =
                                listOf(
                                    experience
                                ) +
                                        _uiState
                                            .value
                                            .experiences,
                            message =
                                "Experiencia agregada."
                        )
                }
                .onFailure {

                    _uiState.value =
                        _uiState.value.copy(
                            isSaving = false,
                            message =
                                it.message
                                    ?: "No se pudo agregar la experiencia."
                        )
                }
        }
    }

    fun clearMessage() {

        _uiState.value =
            _uiState.value.copy(
                message = null
            )
    }
}