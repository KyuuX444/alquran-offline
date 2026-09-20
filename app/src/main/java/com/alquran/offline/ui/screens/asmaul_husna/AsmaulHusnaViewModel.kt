package com.alquran.offline.ui.screens.asmaul_husna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.AsmaulHusnaRepository
import com.alquran.offline.model.AsmaulHusna
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class AsmaulHusnaViewModel(
    private val repository: AsmaulHusnaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val asmaulHusnaList: StateFlow<List<AsmaulHusna>> = _searchQuery
        .flatMapLatest { query ->
            repository.searchAsmaulHusna(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    class Factory(
        private val repository: AsmaulHusnaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AsmaulHusnaViewModel::class.java)) {
                return AsmaulHusnaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
