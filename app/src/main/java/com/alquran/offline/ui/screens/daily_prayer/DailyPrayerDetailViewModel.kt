package com.alquran.offline.ui.screens.daily_prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.DailyPrayerRepository
import com.alquran.offline.model.DailyPrayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DailyPrayerDetailViewModel(
    private val repository: DailyPrayerRepository,
    private val preferencesRepository: UserPreferencesRepository,
    val initialPrayerId: String
) : ViewModel() {

    private val _currentPrayer = MutableStateFlow<DailyPrayer?>(null)
    val currentPrayer: StateFlow<DailyPrayer?> = _currentPrayer.asStateFlow()

    private val _allPrayers = MutableStateFlow<List<DailyPrayer>>(emptyList())
    val allPrayers: StateFlow<List<DailyPrayer>> = _allPrayers.asStateFlow()

    val arabicFontSize = preferencesRepository.arabicFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 28f
    )

    val translationFontSize = preferencesRepository.translationFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 15f
    )

    init {
        loadData(initialPrayerId)
    }

    private fun loadData(targetId: String) {
        viewModelScope.launch {
            val list = repository.getAllPrayersList()
            _allPrayers.value = list
            val found = list.firstOrNull { it.id.equals(targetId, ignoreCase = true) }
                ?: list.firstOrNull()
            _currentPrayer.value = found
        }
    }

    fun navigateToPrayer(id: String) {
        val list = _allPrayers.value
        val found = list.firstOrNull { it.id.equals(id, ignoreCase = true) }
        if (found != null) {
            _currentPrayer.value = found
        }
    }

    fun navigateNext() {
        val list = _allPrayers.value
        val current = _currentPrayer.value ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex in 0 until list.size - 1) {
            _currentPrayer.value = list[currentIndex + 1]
        }
    }

    fun navigatePrevious() {
        val list = _allPrayers.value
        val current = _currentPrayer.value ?: return
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) {
            _currentPrayer.value = list[currentIndex - 1]
        }
    }

    fun updateArabicFontSize(size: Float) {
        viewModelScope.launch {
            preferencesRepository.setArabicFontSize(size.coerceIn(20f, 44f))
        }
    }

    fun updateTranslationFontSize(size: Float) {
        viewModelScope.launch {
            preferencesRepository.setTranslationFontSize(size.coerceIn(12f, 24f))
        }
    }

    class Factory(
        private val repository: DailyPrayerRepository,
        private val preferencesRepository: UserPreferencesRepository,
        private val prayerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyPrayerDetailViewModel::class.java)) {
                return DailyPrayerDetailViewModel(repository, preferencesRepository, prayerId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
