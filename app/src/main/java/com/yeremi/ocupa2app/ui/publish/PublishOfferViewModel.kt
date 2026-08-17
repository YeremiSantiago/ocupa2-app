package com.yeremi.ocupa2app.ui.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.PublishOfferRepository
import com.yeremi.ocupa2app.data.repository.PublishResult
import com.yeremi.ocupa2app.network.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class PublishStep { DATOS, FOTO, PAGO, EXITO }

data class PublishUiState(
    val step: PublishStep = PublishStep.DATOS,
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,
    val errorMessage: String? = null,

    // Paso 1: datos
    val jobTypes: List<JobTypeDetail> = emptyList(),
    val selectedJobType: JobTypeDetail? = null,
    val contractType: String = "temporal",   // "temporal" | "fijo" | "horas"
    val address: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val paymentAmount: String = "",
    val description: String = "",
    val deadline: String = "",
    val additionalQuestions: List<PublishQuestion> = emptyList(),
    val customAnswers: Map<String, String> = emptyMap(),

    // Paso 2: foto (solo local, se sube en el paso de pago)
    val photoFile: File? = null,

    // Paso 3: pago
    val cardNumber: String = "",
    val expMonth: String = "",
    val expYear: String = "",
    val cvv: String = "",
    val cardholder: String = ""
)

class PublishOfferViewModel(private val repository: PublishOfferRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState

    fun loadJobTypes() {
        viewModelScope.launch {
            when (val result = repository.getJobTypes()) {
                is PublishResult.Success -> _uiState.value = _uiState.value.copy(jobTypes = result.data)
                is PublishResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun selectJobType(jobType: JobTypeDetail) {
        if (_uiState.value.selectedJobType?.key == jobType.key) return
        _uiState.value = _uiState.value.copy(selectedJobType = jobType, customAnswers = emptyMap())
    }

    fun updateContractType(value: String) { _uiState.value = _uiState.value.copy(contractType = value) }
    fun updateAddress(value: String) { _uiState.value = _uiState.value.copy(address = value) }
    fun updateLocation(lat: Double, lng: Double) { _uiState.value = _uiState.value.copy(lat = lat, lng = lng) }
    fun updatePaymentAmount(value: String) { _uiState.value = _uiState.value.copy(paymentAmount = value) }
    fun updateDescription(value: String) { _uiState.value = _uiState.value.copy(description = value) }
    fun updateDeadline(value: String) { _uiState.value = _uiState.value.copy(deadline = value) }

    fun updateCustomAnswer(key: String, value: String) {
        val current = _uiState.value.customAnswers.toMutableMap()
        current[key] = value
        _uiState.value = _uiState.value.copy(customAnswers = current)
    }

    fun addQuestion(question: PublishQuestion) {
        _uiState.value = _uiState.value.copy(additionalQuestions = _uiState.value.additionalQuestions + question)
    }

    fun removeQuestion(index: Int) {
        val updated = _uiState.value.additionalQuestions.toMutableList()
        if (index in updated.indices) updated.removeAt(index)
        _uiState.value = _uiState.value.copy(additionalQuestions = updated)
    }

    fun validateStepDatos(): Boolean {
        val s = _uiState.value
        val isValid = s.selectedJobType != null &&
                s.address.isNotBlank() &&
                s.lat != null && s.lng != null &&
                s.paymentAmount.toDoubleOrNull() != null &&
                s.description.length >= 10 &&
                s.deadline.isNotBlank()

        if (!isValid) {
            val msg = if (s.description.isNotBlank() && s.description.length < 10) {
                "La descripción es muy corta (mínimo 10 caracteres)"
            } else {
                "Completa todos los campos obligatorios y selecciona la ubicación"
            }
            _uiState.value = s.copy(errorMessage = msg)
        }
        return isValid
    }

    fun goToStep(step: PublishStep) {
        _uiState.value = _uiState.value.copy(step = step, errorMessage = null)
    }

    fun setPhoto(file: File) {
        _uiState.value = _uiState.value.copy(photoFile = file, errorMessage = null)
    }

    fun confirmPhotoAndGoToPago() {
        if (_uiState.value.photoFile == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "La foto del empleo es obligatoria")
            return
        }
        goToStep(PublishStep.PAGO)
    }

    fun updateCard(cardNumber: String? = null, expMonth: String? = null, expYear: String? = null, cvv: String? = null, cardholder: String? = null) {
        val s = _uiState.value
        _uiState.value = s.copy(
            cardNumber = cardNumber ?: s.cardNumber,
            expMonth = expMonth ?: s.expMonth,
            expYear = expYear ?: s.expYear,
            cvv = cvv ?: s.cvv,
            cardholder = cardholder ?: s.cardholder
        )
    }

    // Cadena completa: cobra -> sube foto -> crea oferta
    fun payAndPublish() {
        val s = _uiState.value
        val photo = s.photoFile
        val jobType = s.selectedJobType

        if (photo == null || jobType == null) {
            _uiState.value = s.copy(errorMessage = "Faltan datos de la oferta o la foto")
            return
        }
        if (s.cardNumber.isBlank() || s.expMonth.isBlank() || s.expYear.isBlank() || s.cvv.isBlank() || s.cardholder.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Completa todos los datos de la tarjeta")
            return
        }

        _uiState.value = s.copy(isLoading = true, errorMessage = null, loadingMessage = "Procesando pago...")

        viewModelScope.launch {
            // 1. Cobrar
            val chargeRequest = ChargeCardRequest(
                cardNumber = s.cardNumber,
                cvv = s.cvv,
                expMonth = s.expMonth.toIntOrNull() ?: 0,
                expYear = s.expYear.toIntOrNull() ?: 0,
                cardholder = s.cardholder
            )
            when (val chargeResult = repository.chargeCard(chargeRequest)) {
                is PublishResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, loadingMessage = null, errorMessage = chargeResult.message)
                    return@launch
                }
                is PublishResult.Success -> {
                    val paymentId = chargeResult.data.id

                    // 2. Subir foto
                    _uiState.value = _uiState.value.copy(loadingMessage = "Subiendo foto...")
                    when (val photoResult = repository.uploadPhoto(photo)) {
                        is PublishResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                loadingMessage = null,
                                errorMessage = "Pago aprobado, pero falló la foto: ${photoResult.message}"
                            )
                            return@launch
                        }
                        is PublishResult.Success -> {
                            val photoUrl = photoResult.data

                            // 3. Crear la oferta
                            _uiState.value = _uiState.value.copy(loadingMessage = "Publicando oferta...")
                            val createRequest = CreateOfferRequest(
                                jobTypeKey = jobType.key,
                                contractType = s.contractType,
                                description = s.description,
                                address = s.address,
                                photo = photoUrl,
                                paymentId = paymentId,
                                location = OfferLocationBody(lat = s.lat ?: 0.0, lng = s.lng ?: 0.0),
                                payment = OfferPaymentBody(amount = s.paymentAmount.toDoubleOrNull() ?: 0.0),
                                deadline = s.deadline,
                                customAnswers = s.customAnswers,
                                questions = s.additionalQuestions
                            )
                            when (val createResult = repository.createOffer(createRequest)) {
                                is PublishResult.Success -> _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    loadingMessage = null,
                                    step = PublishStep.EXITO
                                )
                                is PublishResult.Error -> _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    loadingMessage = null,
                                    errorMessage = "Pago aprobado, pero falló crear la oferta: ${createResult.message}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun reset() {
        _uiState.value = PublishUiState(jobTypes = _uiState.value.jobTypes)
    }
}