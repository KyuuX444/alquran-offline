package com.alquran.offline.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Hadith
import com.alquran.offline.model.LastRead
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: QuranRepository
) : ViewModel() {

    val lastRead: StateFlow<LastRead> = repository.lastRead.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LastRead(1, "Al-Fatihah", 1)
    )

    private val _todayHadith = MutableStateFlow<Hadith?>(null)
    val todayHadith: StateFlow<Hadith?> = _todayHadith.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _todayHadith.value = repository.getTodayHadith()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val repository: QuranRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
