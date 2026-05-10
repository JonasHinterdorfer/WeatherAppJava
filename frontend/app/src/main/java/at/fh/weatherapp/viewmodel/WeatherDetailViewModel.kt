package at.fh.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.fh.weatherapp.data.model.WeatherDetail
import at.fh.weatherapp.data.repository.Result
import at.fh.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherDetailUiState(
    val weather: WeatherDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WeatherDetailViewModel(
    private val cityId: Long,
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailUiState(isLoading = true))
    val uiState: StateFlow<WeatherDetailUiState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getWeather(cityId)) {
                is Result.Success -> {
                    _uiState.value = WeatherDetailUiState(
                        weather = result.data,
                        isLoading = false
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
    }

    class Factory(private val cityId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeatherDetailViewModel(cityId) as T
        }
    }
}
