package dev.five_star.trackingapp.feature.observer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.five_star.trackingapp.core.location.data.LocationHistoryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads the uploaded locations of [id] once; every detail entry gets its own instance. */
class ObserverDetailViewModel(
    private val id: String,
    private val locationHistoryRepository: LocationHistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ObserverDetailState>(ObserverDetailState.Loading)
    val state: StateFlow<ObserverDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                ObserverDetailState.Success(locationHistoryRepository.load(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // e.g. no connection, missing permission or malformed data in Firebase
                Log.w(TAG, "Loading the locations of $id failed", e)
                ObserverDetailState.Error
            }
        }
    }

    private companion object {
        const val TAG = "ObserverDetailViewModel"
    }
}

class ObserverDetailViewModelFactory(
    private val id: String,
    private val locationHistoryRepository: LocationHistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObserverDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObserverDetailViewModel(id, locationHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
