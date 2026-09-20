package com.alquran.offline.ui.screens.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.PrayerRepository
import com.alquran.offline.model.PrayerReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PrayerDetailViewModel(
    private val repository: PrayerRepository,
    private val preferencesRepository: UserPreferencesRepository,
    initialReadingId: Int
) : ViewModel() {

    private val _currentId = MutableStateFlow(initialReadingId)
    val currentId: StateFlow<Int> = _currentId.asStateFlow()

    private val _currentReading = MutableStateFlow<PrayerReading?>(null)
    val currentReading: StateFlow<PrayerReading?> = _currentReading.asStateFlow()

    private val _allReadings = MutableStateFlow<List<PrayerReading>>(emptyList())
    val allReadings: StateFlow<List<PrayerReading>> = _allReadings.asStateFlow()

    val arabicFontSize: StateFlow<Float> = preferencesRepository.arabicFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 26f
    )

    init {
        viewModelScope.launch {
            val list = repository.getAllReadingsList()
            _allReadings.value = list
            loadReading(_currentId.value, list)
        }
    }

    fun selectReading(id: Int) {
        _currentId.value = id
        loadReading(id, _allReadings.value)
    }

    fun nextReading() {
        val list = _allReadings.value
        val currentIndex = list.indexOfFirst { it.id == _currentId.value }
        if (currentIndex in 0 until list.size - 1) {
            val next = list[currentIndex + 1]
            selectReading(next.id)
        }
    }

    fun previousReading() {
        val list = _allReadings.value
        val currentIndex = list.indexOfFirst { it.id == _currentId.value }
        if (currentIndex > 0) {
            val prev = list[currentIndex - 1]
            selectReading(prev.id)
        }
    }

    private fun loadReading(id: Int, list: List<PrayerReading>) {
        val found = list.firstOrNull { it.id == id } ?: list.firstOrNull()
        _currentReading.value = found
    }

    class Factory(
        private val repository: PrayerRepository,
        private val preferencesRepository: UserPreferencesRepository,
        private val initialReadingId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrayerDetailViewModel(repository, preferencesRepository, initialReadingId) as T
        }
    }
}
