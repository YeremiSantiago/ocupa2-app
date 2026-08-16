package com.yeremi.ocupa2app.ui.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ZoibeRepository
import com.yeremi.ocupa2app.network.models.OfferDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OfferDetailUiState(
    val isLoading: Boolean = false,
    val offer: OfferDetail? = null,
    val error: String? = null,
    val isApplying: Boolean = false,
    val applySuccess: Boolean = false,
    val message: String? = null
)

class OfferDetailViewModel(
    private val repository: ZoibeRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            OfferDetailUiState()
        )

    val uiState:
            StateFlow<OfferDetailUiState> =
        _uiState

    fun loadOffer(
        offerId: String
    ) {

        if (offerId.isBlank()) {
            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    message = null
                )

            repository
                .getOfferDetail(offerId)
                .onSuccess { offer ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            offer = offer,
                            error = null
                        )
                }
                .onFailure {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error =
                                it.message
                                    ?: "No se pudo cargar la oferta"
                        )
                }
        }
    }

    fun apply(
        comment: String,
        answers: Map<String, String>
    ) {

        val offer =
            _uiState.value.offer
                ?: return

        if (comment.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    message =
                        "Escribe por qué te consideras apto para el puesto."
                )

            return
        }

        val missingRequired =
            offer.questions.any { question ->

                question.required &&
                        answers[
                            question.id
                                ?: question.label
                        ].isNullOrBlank()
            }

        if (missingRequired) {

            _uiState.value =
                _uiState.value.copy(
                    message =
                        "Completa todas las preguntas obligatorias."
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isApplying = true,
                    message = null
                )

            repository.applyToOffer(
                offerId = offer.id,
                comment = comment,
                answers = answers
            )
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            isApplying = false,
                            applySuccess = true,
                            message =
                                "Aplicación enviada correctamente.",
                            offer =
                                offer.copy(
                                    appliedByMe = true
                                )
                        )
                }
                .onFailure {

                    _uiState.value =
                        _uiState.value.copy(
                            isApplying = false,
                            message =
                                it.message
                                    ?: "No se pudo enviar la aplicación."
                        )
                }
        }
    }
}