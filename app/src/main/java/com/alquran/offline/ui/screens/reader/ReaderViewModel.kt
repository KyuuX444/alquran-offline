package com.alquran.offline.ui.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.Ayah
import com.alquran.offline.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val repository: QuranRepository,
    rawSurahId: Int,
    rawInitialTargetVerse: Int = 1
) : ViewModel() {

    val surahId: Int = rawSurahId.coerceIn(1, 114)
    val initialTargetVerse: Int = rawInitialTargetVerse.coerceAtLeast(1)

    private val _surah = MutableStateFlow<Surah?>(null)
    val surah: StateFlow<Surah?> = _surah.asStateFlow()

    val ayahs: StateFlow<List<Ayah>> = repository.getAyahsBySurah(surahId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val arabicFontSize: StateFlow<Float> = repository.arabicFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 28f
    )

    val translationFontSize: StateFlow<Float> = repository.translationFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 15f
    )

    val showTranslation: StateFlow<Boolean> = repository.showTranslation.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    init {
        loadSurah()
    }

    private fun loadSurah() {
        viewModelScope.launch {
            try {
                val s = repository.getSurahById(surahId)
                _surah.value = s
                if (s != null) {
                    // Auto-save initial last read position
                    repository.saveLastRead(surahId, s.nameLatin, initialTargetVerse)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun toggleBookmark(ayah: Ayah) {
        viewModelScope.launch {
            try {
                if (ayah.isBookmarked) {
                    repository.removeBookmark(ayah.surahId, ayah.verseId)
                } else {
                    repository.addBookmark(ayah.surahId, ayah.verseId)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private var lastSavedVerseId: Int = -1

    fun updateLastRead(verseId: Int) {
        val currentSurah = _surah.value ?: return
        val safeVerse = verseId.coerceAtLeast(1)
        if (lastSavedVerseId == safeVerse) return
        lastSavedVerseId = safeVerse
        viewModelScope.launch {
            try {
                repository.saveLastRead(surahId, currentSurah.nameLatin, safeVerse)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    class Factory(
        private val repository: QuranRepository,
        private val surahId: Int,
        private val initialTargetVerse: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReaderViewModel(repository, surahId, initialTargetVerse) as T
        }
    }
}
