package com.alquran.offline.ui.screens.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Hadith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HadithDetailViewModel(
    private val repository: QuranRepository,
    val hadithId: Int
) : ViewModel() {

    private val _hadith = MutableStateFlow<Hadith?>(null)
    val hadith: StateFlow<Hadith?> = _hadith.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHadith()
    }

    fun loadHadith() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val h = repository.getHadithById(hadithId)
                _hadith.value = h
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleBookmark() {
        val current = _hadith.value ?: return
        viewModelScope.launch {
            repository.toggleHadithBookmark(current.id)
            _hadith.value = current.copy(isBookmarked = !current.isBookmarked)
        }
    }

    class Factory(
        private val repository: QuranRepository,
        private val hadithId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HadithDetailViewModel(repository, hadithId) as T
        }
    }
}
