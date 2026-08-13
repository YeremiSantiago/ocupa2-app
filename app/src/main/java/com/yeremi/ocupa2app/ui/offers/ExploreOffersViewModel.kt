package com.yeremi.ocupa2app.ui.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeremi.ocupa2app.data.repository.ProfileRepository
import com.yeremi.ocupa2app.network.models.JobType
import com.yeremi.ocupa2app.network.models.Offer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreOffersViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _jobTypes = MutableStateFlow<List<JobType>>(emptyList())
    val jobTypes: StateFlow<List<JobType>> = _jobTypes

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedJobTypeId = MutableStateFlow<Int?>(null)
    val selectedJobTypeId: StateFlow<Int?> = _selectedJobTypeId

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var currentPage = 1
    private var hasMorePages = true
    private var searchJob: Job? = null

    init {
        loadJobTypes()
        loadOffers()
    }

    private fun loadJobTypes() {
        viewModelScope.launch {
            repository.getJobTypes().onSuccess {
                _jobTypes.value = it
            }
        }
    }

    fun loadOffers(isRefresh: Boolean = true) {
        if (isRefresh) {
            currentPage = 1
            hasMorePages = true
        } else if (!hasMorePages || _isLoadingMore.value) {
            return
        }

        viewModelScope.launch {
            if (isRefresh) _isLoading.value = true else _isLoadingMore.value = true
            
            repository.getOffers(_searchQuery.value, _selectedJobTypeId.value, currentPage)
                .onSuccess { response ->
                    if (isRefresh) {
                        _offers.value = response.offers
                    } else {
                        _offers.value = _offers.value + response.offers
                    }
                    currentPage++
                    hasMorePages = currentPage <= response.totalPages
                    _errorMessage.value = null
                }
                .onFailure {
                    _errorMessage.value = it.message
                }
            
            if (isRefresh) _isLoading.value = false else _isLoadingMore.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadOffers()
        }
    }

    fun onJobTypeSelected(id: Int?) {
        _selectedJobTypeId.value = id
        loadOffers()
    }

    fun toggleLike(offerId: Int) {
        val offer = _offers.value.find { it.id == offerId } ?: return
        val currentLiked = offer.isLiked
        
        // Optimistic update
        _offers.value = _offers.value.map {
            if (it.id == offerId) it.copy(isLiked = !currentLiked) else it
        }

        viewModelScope.launch {
            repository.toggleLike(offerId, currentLiked).onFailure {
                // Revert on failure
                _offers.value = _offers.value.map {
                    if (it.id == offerId) it.copy(isLiked = currentLiked) else it
                }
            }
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedJobTypeId.value = null
        loadOffers()
    }
}
