package com.yeremi.ocupa2app.ui.myoffers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.MyOffersRepository
import com.yeremi.ocupa2app.data.repository.MyOffersResult
import com.yeremi.ocupa2app.network.models.Applicant
import com.yeremi.ocupa2app.network.models.MyOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MyOffersUiState(
    val isLoading: Boolean = false,
    val myOffers: List<MyOffer> = emptyList(),
    val applicants: List<Applicant> = emptyList(),
    val actionInProgressId: String? = null,
    val errorMessage: String? = null
)

class MyOffersViewModel(private val repository: MyOffersRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MyOffersUiState())
    val uiState: StateFlow<MyOffersUiState> = _uiState

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun loadMyOffers() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getMyOffers()) {
                is MyOffersResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, myOffers = result.data)
                is MyOffersResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun loadApplicants(offerId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getApplicants(offerId)) {
                is MyOffersResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, applicants = result.data)
                is MyOffersResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun deactivateOffer(offerId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.deactivateOffer(offerId)) {
                is MyOffersResult.Success -> { loadMyOffers(); onDone() }
                is MyOffersResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun rateApplicant(offerId: String, applicationId: String, rating: Int) = runAction(offerId, applicationId) {
        repository.rateApplicant(applicationId, rating)
    }

    fun discardApplicant(offerId: String, applicationId: String) = runAction(offerId, applicationId) {
        repository.discardApplicant(applicationId)
    }

    fun markFinalist(offerId: String, applicationId: String) = runAction(offerId, applicationId) {
        repository.markFinalist(applicationId)
    }

    fun chooseWinner(
        offerId: String,
        applicationId: String,
        salary: Double? = null,
        currency: String? = null,
        startDate: String? = null,
        duration: String? = null
    ) = runAction(offerId, applicationId) {
        repository.chooseWinner(applicationId, salary, currency, startDate, duration)
    }

    private fun runAction(offerId: String, applicationId: String, action: suspend () -> MyOffersResult<Unit>) {
        _uiState.value = _uiState.value.copy(actionInProgressId = applicationId)
        viewModelScope.launch {
            when (val result = action()) {
                is MyOffersResult.Success -> {
                    _uiState.value = _uiState.value.copy(actionInProgressId = null)
                    loadApplicants(offerId)
                }
                is MyOffersResult.Error -> _uiState.value = _uiState.value.copy(actionInProgressId = null, errorMessage = result.message)
            }
        }
    }
}