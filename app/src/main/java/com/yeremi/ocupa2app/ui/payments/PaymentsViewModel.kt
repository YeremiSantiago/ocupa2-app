package com.yeremi.ocupa2app.ui.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ZoibeRepository
import com.yeremi.ocupa2app.network.models.Payment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PaymentsUiState(
    val isLoading:
    Boolean = false,
    val payments:
    List<Payment> =
        emptyList(),
    val error:
    String? = null
)

class PaymentsViewModel(
    private val repository:
    ZoibeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            PaymentsUiState()
        )

    val uiState:
            StateFlow<PaymentsUiState> =
        _uiState

    init {
        loadPayments()
    }

    fun loadPayments() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            repository
                .getPayments()
                .onSuccess {

                    _uiState.value =
                        PaymentsUiState(
                            payments =
                                it
                        )
                }
                .onFailure {

                    _uiState.value =
                        PaymentsUiState(
                            error =
                                it.message
                                    ?: "No se pudieron cargar los pagos."
                        )
                }
        }
    }
}