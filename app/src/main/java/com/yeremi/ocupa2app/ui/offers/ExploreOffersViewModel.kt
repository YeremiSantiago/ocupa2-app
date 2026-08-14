package com.yeremi.ocupa2app.ui.offers

import android.util.Log
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

    companion object {
        private const val TAG = "ExploreOffersVM"
        private const val PAGE_LIMIT = 10
    }

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

    private val _selectedJobTypeId = MutableStateFlow<String?>(null)
    val selectedJobTypeId: StateFlow<String?> = _selectedJobTypeId

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
            Log.d(TAG, "→ GET /job-types")
            repository.getJobTypes()
                .onSuccess { types ->
                    Log.d(TAG, "✓ job-types: ${types.size} tipos → $types")
                    _jobTypes.value = types
                }
                .onFailure { error ->
                    Log.e(TAG, "✗ job-types error: ${error.message}")
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

            val searchParam = _searchQuery.value.trim().ifEmpty { null }
            Log.d(TAG, "→ GET /offers | search=$searchParam | jobTypeId=${_selectedJobTypeId.value} | page=$currentPage")

            repository.getOffers(searchParam, _selectedJobTypeId.value, currentPage, PAGE_LIMIT)
                .onSuccess { result ->
                    Log.d(TAG, "✓ offers recibidos=${result.offers.size} | hasMore=${result.hasMore}")
                    if (isRefresh) {
                        _offers.value = result.offers
                    } else {
                        _offers.value = _offers.value + result.offers
                    }
                    currentPage++
                    hasMorePages = result.hasMore
                    _errorMessage.value = null
                }
                .onFailure { error ->
                    Log.e(TAG, "✗ offers error: ${error.message}")
                    _errorMessage.value = error.message
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

    fun onJobTypeSelected(id: String?) {
        _selectedJobTypeId.value = id
        loadOffers()
    }

    fun toggleLike(offerId: String) {
        val offer = _offers.value.find { it.id == offerId } ?: return
        val currentLiked = offer.likedByMe

        // Optimistic update
        _offers.value = _offers.value.map {
            if (it.id == offerId) it.copy(likedByMe = !currentLiked) else it
        }

        viewModelScope.launch {
            repository.toggleLike(offerId, currentLiked).onFailure {
                Log.e(TAG, "✗ toggleLike error: ${it.message}")
                _offers.value = _offers.value.map { o ->
                    if (o.id == offerId) o.copy(likedByMe = currentLiked) else o
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
