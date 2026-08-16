package com.yeremi.ocupa2app.ui.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ZoibeRepository
import com.yeremi.ocupa2app.network.models.MyApplicationDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MyApplicationsUiState(
    val isLoading: Boolean = false,
    val applications:
    List<MyApplicationDetail> =
        emptyList(),
    val error: String? = null
)

class MyApplicationsViewModel(
    private val repository:
    ZoibeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            MyApplicationsUiState()
        )

    val uiState:
            StateFlow<MyApplicationsUiState> =
        _uiState

    init {
        loadApplications()
    }

    fun loadApplications() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            repository
                .getMyApplications()
                .onSuccess {

                    _uiState.value =
                        MyApplicationsUiState(
                            applications = it
                        )
                }
                .onFailure {

                    _uiState.value =
                        MyApplicationsUiState(
                            error =
                                it.message
                                    ?: "No se pudieron cargar tus aplicaciones."
                        )
                }
        }
    }
}