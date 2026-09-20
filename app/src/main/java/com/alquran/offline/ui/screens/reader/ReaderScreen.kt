package com.alquran.offline.ui.screens.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alquran.offline.model.Ayah
import com.alquran.offline.ui.components.AppTopBar
import com.alquran.offline.ui.components.AyahCard
import com.alquran.offline.ui.components.BasmalahHeader
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val surah by viewModel.surah.collectAsState()
    val ayahs by viewModel.ayahs.collectAsState()
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()
    val translationFontSize by viewModel.translationFontSize.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()

    val listState = rememberLazyListState()

    // Scroll to target verse when ayahs load
    LaunchedEffect(ayahs.isNotEmpty()) {
        if (ayahs.isNotEmpty() && viewModel.initialTargetVerse > 1) {
            val targetIndex = (viewModel.initialTargetVerse - 1).coerceIn(0, ayahs.size - 1)
            // Account for Basmalah banner header if present (surahId != 1 && surahId != 9)
            val headerOffset = if (viewModel.surahId != 1 && viewModel.surahId != 9) 1 else 0
            listState.animateScrollToItem(targetIndex + headerOffset)
        }
    }

    // Auto-update Last Read as user scrolls
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    LaunchedEffect(firstVisibleIndex) {
        if (ayahs.isNotEmpty()) {
            val headerOffset = if (viewModel.surahId != 1 && viewModel.surahId != 9) 1 else 0
            val ayahIndex = (firstVisibleIndex - headerOffset).coerceIn(0, ayahs.size - 1)
            val currentAyah = ayahs.getOrNull(ayahIndex)
            if (currentAyah != null) {
                viewModel.updateLastRead(currentAyah.verseId)
            }
        }
    }

    fun copyAyahToClipboard(ayah: Ayah) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            appendLine(ayah.textAr)
            if (ayah.transliteration.isNotBlank()) appendLine(ayah.transliteration)
            if (ayah.textId.isNotBlank()) appendLine(ayah.textId)
            appendLine("(${surah?.nameLatin ?: "Surah ${ayah.surahId}"}: ${ayah.verseId})")
        }
        val clip = ClipData.newPlainText("Ayat Al-Qur'an", text)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState.showSnackbar("Ayat berhasil disalin ke papan klip")
        }
    }

    fun shareAyah(ayah: Ayah) {
        val shareText = buildString {
            appendLine(ayah.textAr)
            appendLine()
            if (ayah.transliteration.isNotBlank()) {
                appendLine(ayah.transliteration)
                appendLine()
            }
            if (ayah.textId.isNotBlank()) {
                appendLine("\"${ayah.textId}\"")
                appendLine()
            }
            appendLine("— QS. ${surah?.nameLatin ?: "Surah ${ayah.surahId}"} [${ayah.surahId}:${ayah.verseId}]")
            appendLine("Dibaca via Al-Qur'an Offline")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Ayat")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = surah?.let { "${it.id}. ${it.nameLatin}" } ?: "Membaca Surah",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Tampilan"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (ayahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Basmalah Banner for surahs other than Surah 1 (Al-Fatihah) and Surah 9 (At-Tawbah)
                if (viewModel.surahId != 1 && viewModel.surahId != 9) {
                    item {
                        BasmalahHeader()
                    }
                }

                itemsIndexed(
                    items = ayahs,
                    key = { _, ayah -> ayah.id }
                ) { _, ayah ->
                    AyahCard(
                        ayah = ayah,
                        arabicFontSize = arabicFontSize,
                        translationFontSize = translationFontSize,
                        showTranslation = showTranslation,
                        onBookmarkClick = {
                            viewModel.toggleBookmark(ayah)
                            scope.launch {
                                val msg = if (ayah.isBookmarked) "Bookmark dihapus" else "Ayat ditambahkan ke bookmark"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onCopyClick = { copyAyahToClipboard(ayah) },
                        onShareClick = { shareAyah(ayah) }
                    )
                }
            }
        }
    }
}
