package com.alquran.offline.ui.screens.juz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.JuzInfo

class JuzListViewModel(
    private val repository: QuranRepository
) : ViewModel() {

    val allJuz: List<JuzInfo> = repository.getAllJuz()

    class Factory(private val repository: QuranRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return JuzListViewModel(repository) as T
        }
    }
}
