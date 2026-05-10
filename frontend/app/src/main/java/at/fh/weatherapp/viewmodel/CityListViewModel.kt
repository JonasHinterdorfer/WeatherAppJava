package at.fh.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.fh.weatherapp.data.model.City
import at.fh.weatherapp.data.repository.Result
import at.fh.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CityListUiState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CityListViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityListUiState(isLoading = true))
    val uiState: StateFlow<CityListUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startPolling()
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchCities()
                delay(5_000L)
            }
        }
    }

    private suspend fun fetchCities() {
        // Only show loading spinner on first load (when list is empty)
        if (_uiState.value.cities.isEmpty()) {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        when (val result = repository.getCities()) {
            is Result.Success -> {
                _uiState.value = CityListUiState(
                    cities = result.data,
                    isLoading = false,
                    errorMessage = null
                )
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun retry() {
        viewModelScope.launch { fetchCities() }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
