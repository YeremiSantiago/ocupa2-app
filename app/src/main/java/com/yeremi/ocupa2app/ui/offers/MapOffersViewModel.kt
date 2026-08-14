package com.yeremi.ocupa2app.ui.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import com.yeremi.ocupa2app.network.models.Offer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapOffersViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    private val _selectedOffer = MutableStateFlow<Offer?>(null)
    val selectedOffer: StateFlow<Offer?> = _selectedOffer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    // Controla si el overlay de permisos es visible.
    // false = el usuario eligió "Ver sin ubicación" o ya tiene permiso.
    private val _showPermissionOverlay = MutableStateFlow(true)
    val showPermissionOverlay: StateFlow<Boolean> = _showPermissionOverlay

    init {
        loadOffers()
    }

    fun loadOffers() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getOffers(null, null, 1, limit = 50)
                .onSuccess { result ->
                    // Filtrar ofertas sin coordenadas válidas
                    _offers.value = result.offers.filter { offer ->
                        val lat = offer.location?.lat
                        val lng = offer.location?.lng
                        lat != null && lng != null && lat != 0.0 && lng != 0.0
                    }
                    if (_selectedOffer.value == null && _offers.value.isNotEmpty()) {
                        _selectedOffer.value = _offers.value.first()
                    }
                    _errorMessage.value = null
                }
                .onFailure {
                    _errorMessage.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun selectOffer(offer: Offer) {
        _selectedOffer.value = offer
    }

    fun updateLocationPermission(granted: Boolean) {
        _hasLocationPermission.value = granted
        // Si se concedió el permiso, ocultar el overlay automáticamente
        if (granted) {
            _showPermissionOverlay.value = false
        }
    }

    fun updateUserLocation(latLng: LatLng) {
        _userLocation.value = latLng
    }

    /** Oculta el overlay de permisos — se llama con "Ver sin ubicación" */
    fun dismissPermissionOverlay() {
        _showPermissionOverlay.value = false
    }
}
