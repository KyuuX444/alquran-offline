package com.alquran.offline.ui.screens.daily_prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.DailyPrayerRepository
import com.alquran.offline.model.DailyPrayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DailyPrayerListViewModel(
    private val repository: DailyPrayerRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Semua")

    val categories: StateFlow<List<String>> = repository.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("Semua")
    )

    private val allPrayers = repository.getAllPrayers()

    val filteredPrayers: StateFlow<List<DailyPrayer>> = combine(
        allPrayers,
        searchQuery,
        selectedCategory
    ) { prayers, query, category ->
        val byCategory = if (category == "Semua" || category.isBlank()) {
            prayers
        } else {
            prayers.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isBlank()) {
            byCategory
        } else {
            val q = query.trim().lowercase()
            byCategory.filter {
                it.title.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    (it.transliteration?.lowercase()?.contains(q) == true) ||
                    (it.translation?.lowercase()?.contains(q) == true) ||
                    (it.source?.lowercase()?.contains(q) == true) ||
                    it.arabic.contains(q)
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

    class Factory(
        private val repository: DailyPrayerRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyPrayerListViewModel::class.java)) {
                return DailyPrayerListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
