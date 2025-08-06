package dev.five_star.trackingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ExampleViewModel: ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExampleViewModel() as T
        }
    }

    private val _state = MutableStateFlow(ExampleState())
    val state = _state
        .onStart { loadData() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ExampleState()
        )

    fun onAction(action: ExampleAction) {
        when (action) {
            ExampleAction.OnButtonClicked -> greetWorld()
        }
    }

    private fun greetWorld() {
        _state.value = _state.value.copy(greeting = "Hello World!")
    }

    private fun loadData() {

    }
}