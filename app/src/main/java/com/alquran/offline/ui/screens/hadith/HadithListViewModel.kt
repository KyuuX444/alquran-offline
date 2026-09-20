package com.alquran.offline.ui.screens.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Hadith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HadithListViewModel(
    private val repository: QuranRepository,
    val initialHadithId: Int? = null
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val allHadiths = repository.getAllHadiths()

    val filteredHadiths: StateFlow<List<Hadith>> = combine(allHadiths, searchQuery) { hadiths, query ->
        if (query.isBlank()) {
            hadiths
        } else {
            val q = query.trim().lowercase()
            hadiths.filter {
                it.judul.lowercase().contains(q) ||
                it.sumber.lowercase().contains(q) ||
                it.teksId.lowercase().contains(q) ||
                it.nomor.toString() == q ||
                it.teksAr.contains(q)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedHadith = MutableStateFlow<Hadith?>(null)
    val selectedHadith: StateFlow<Hadith?> = _selectedHadith.asStateFlow()

    init {
        if (initialHadithId != null && initialHadithId > 0) {
            viewModelScope.launch {
                val h = repository.getHadithById(initialHadithId)
                _selectedHadith.value = h
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun selectHadith(hadith: Hadith?) {
        _selectedHadith.value = hadith
    }

    fun toggleBookmark(hadith: Hadith) {
        viewModelScope.launch {
            repository.toggleHadithBookmark(hadith.id)
            // Update selected hadith if currently open
            if (_selectedHadith.value?.id == hadith.id) {
                _selectedHadith.value = _selectedHadith.value?.copy(isBookmarked = !hadith.isBookmarked)
            }
        }
    }

    class Factory(
        private val repository: QuranRepository,
        private val initialHadithId: Int? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HadithListViewModel(repository, initialHadithId) as T
        }
    }
}
