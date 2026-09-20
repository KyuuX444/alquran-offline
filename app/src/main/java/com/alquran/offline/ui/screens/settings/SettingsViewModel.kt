package com.alquran.offline.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.ThemeMode
import com.alquran.offline.notification.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: QuranRepository,
    private val appContext: Context
) : ViewModel() {

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

    val themeMode: StateFlow<ThemeMode> = repository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )

    val notificationEnabled: StateFlow<Boolean> = repository.notificationEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val notificationHour: StateFlow<Int> = repository.notificationHour.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 7
    )

    val notificationMinute: StateFlow<Int> = repository.notificationMinute.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun setArabicFontSize(size: Float) {
        viewModelScope.launch { repository.setArabicFontSize(size) }
    }

    fun setTranslationFontSize(size: Float) {
        viewModelScope.launch { repository.setTranslationFontSize(size) }
    }

    fun setShowTranslation(show: Boolean) {
        viewModelScope.launch { repository.setShowTranslation(show) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun resetLastRead() {
        viewModelScope.launch { repository.resetLastRead() }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationEnabled(enabled)
            if (enabled) {
                val h = notificationHour.value
                val m = notificationMinute.value
                NotificationScheduler.scheduleDailyNotification(appContext, h, m)
            } else {
                NotificationScheduler.cancelNotification(appContext)
            }
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setNotificationTime(hour, minute)
            if (notificationEnabled.value) {
                NotificationScheduler.scheduleDailyNotification(appContext, hour, minute)
            }
        }
    }

    class Factory(
        private val repository: QuranRepository,
        private val appContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository, appContext) as T
        }
    }
}
