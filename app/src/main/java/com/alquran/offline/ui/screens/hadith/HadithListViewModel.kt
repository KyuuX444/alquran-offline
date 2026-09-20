package com.alquran.offline.ui.screens.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Hadith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HadithListViewModel(
    private val repository: QuranRepository,
    val initialHadithId: Int? = null
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedKitab = MutableStateFlow("Shahih Bukhari")

    val availableKitabs: StateFlow<List<String>> = repository.getAvailableKitabs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(
            "Shahih Bukhari",
            "Shahih Muslim",
            "Hadits Arba'in An-Nawawi",
            "Sunan Abu Daud",
            "Sunan At-Tirmidzi",
            "Sunan An-Nasa'i",
            "Sunan Ibnu Majah",
            "Muwatha' Malik",
            "Sunan Ad-Darimi",
            "Musnad Ahmad"
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val hadithsForKitab = selectedKitab.flatMapLatest { kitab ->
        repository.getHadithsByKitab(kitab)
    }

    val filteredHadiths: StateFlow<List<Hadith>> = combine(hadithsForKitab, searchQuery) { hadiths, query ->
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
                if (h != null) {
                    selectedKitab.value = h.kitab
                    _selectedHadith.value = h
                }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onKitabSelected(kitab: String) {
        selectedKitab.value = kitab
    }

    fun selectHadith(hadith: Hadith?) {
        _selectedHadith.value = hadith
    }

    fun toggleBookmark(hadith: Hadith) {
        viewModelScope.launch {
            repository.toggleHadithBookmark(hadith.id)
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
