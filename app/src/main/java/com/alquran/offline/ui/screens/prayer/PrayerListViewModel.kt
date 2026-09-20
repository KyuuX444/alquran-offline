package com.alquran.offline.ui.screens.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.PrayerRepository
import com.alquran.offline.model.PrayerReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PrayerListViewModel(
    private val repository: PrayerRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Semua")

    val categories: StateFlow<List<String>> = repository.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("Semua", "Niat", "Rukun Sholat", "Sunnah Sholat", "Dzikir & Doa")
    )

    private val allReadings = repository.getAllReadings()

    val filteredReadings: StateFlow<List<PrayerReading>> = combine(
        allReadings,
        searchQuery,
        selectedCategory
    ) { readings, query, category ->
        val byCategory = if (category == "Semua" || category.isBlank()) {
            readings
        } else {
            readings.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isBlank()) {
            byCategory
        } else {
            val q = query.trim().lowercase()
            byCategory.filter {
                it.title.lowercase().contains(q) ||
                it.translation.lowercase().contains(q) ||
                it.transliteration.lowercase().contains(q) ||
                it.arabic.contains(q) ||
                it.source.lowercase().contains(q)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    class Factory(private val repository: PrayerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrayerListViewModel(repository) as T
        }
    }
}
