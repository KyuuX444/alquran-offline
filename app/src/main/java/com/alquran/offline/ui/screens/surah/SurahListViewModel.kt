package com.alquran.offline.ui.screens.surah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SurahListViewModel(
    private val repository: QuranRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val allSurahs = repository.getAllSurahs()

    val filteredSurahs: StateFlow<List<Surah>> = combine(allSurahs, searchQuery) { surahs, query ->
        if (query.isBlank()) {
            surahs
        } else {
            val q = query.trim().lowercase()
            surahs.filter {
                it.nameLatin.lowercase().contains(q) ||
                it.translationId.lowercase().contains(q) ||
                it.id.toString() == q ||
                it.nameAr.contains(q)
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

    class Factory(private val repository: QuranRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SurahListViewModel(repository) as T
        }
    }
}
