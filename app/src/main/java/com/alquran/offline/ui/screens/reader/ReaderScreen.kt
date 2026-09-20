package com.alquran.offline.ui.screens.reader

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.offline.model.Ayah
import com.alquran.offline.ui.components.AppTopBar
import com.alquran.offline.ui.components.AyahCard
import com.alquran.offline.ui.components.BasmalahHeader
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onNavigateToSurah: (Int) -> Unit = {},
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
            // Account for Surah Info Banner (index 0) + optional Basmalah (index 1)
            val hasBasmalah = viewModel.surahId != 1 && viewModel.surahId != 9
            val headerOffset = 1 + if (hasBasmalah) 1 else 0
            val maxIndex = (ayahs.size + headerOffset - 1).coerceAtLeast(0)
            val safeIndex = (targetIndex + headerOffset).coerceIn(0, maxIndex)
            try {
                listState.animateScrollToItem(safeIndex)
            } catch (e: Throwable) {
                try {
                    listState.scrollToItem(safeIndex)
                } catch (ignored: Throwable) {
                }
            }
        }
    }

    // Auto-update Last Read as user scrolls
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    LaunchedEffect(firstVisibleIndex) {
        if (ayahs.isNotEmpty()) {
            val hasBasmalah = viewModel.surahId != 1 && viewModel.surahId != 9
            val headerOffset = 1 + if (hasBasmalah) 1 else 0
            val ayahIndex = (firstVisibleIndex - headerOffset).coerceIn(0, ayahs.size - 1)
            val currentAyah = ayahs.getOrNull(ayahIndex)
            if (currentAyah != null) {
                viewModel.updateLastRead(currentAyah.verseId)
            }
        }
    }

    fun copyAyahToClipboard(ayah: Ayah) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val text = buildString {
                appendLine(ayah.textAr)
                if (ayah.transliteration.isNotBlank()) appendLine(ayah.transliteration)
                if (ayah.textId.isNotBlank()) appendLine(ayah.textId)
                appendLine("(${surah?.nameLatin ?: "Surah ${ayah.surahId}"}: ${ayah.verseId})")
            }
            val clip = ClipData.newPlainText("Ayat Al-Qur'an", text)
            clipboard?.setPrimaryClip(clip)
            scope.launch {
                snackbarHostState.showSnackbar("Ayat berhasil disalin ke papan klip")
            }
        } catch (e: Throwable) {
            scope.launch {
                snackbarHostState.showSnackbar("Gagal menyalin ayat ke papan klip")
            }
        }
    }

    fun shareAyah(ayah: Ayah) {
        try {
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
            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Ayat").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            scope.launch {
                snackbarHostState.showSnackbar("Tidak ada aplikasi yang dapat menerima konten ini.")
            }
        } catch (e: Throwable) {
            scope.launch {
                snackbarHostState.showSnackbar("Tidak dapat membagikan ayat saat ini.")
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = surah?.nameLatin ?: "Membaca Surah",
                subtitle = surah?.let { "${it.totalVerses} ayat · ${it.type}" },
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Beranda"
                        )
                    }
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
                // Surah Banner Info Header (inspired by com.andi.alquran.id)
                item {
                    surah?.let { s ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = s.nameAr,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = s.nameLatin,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = s.translationId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${s.type} • ${s.totalVerses} Ayat",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

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

                // Surah Navigation: Previous & Next Surah Buttons (com.andi.alquran.id reference)
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.surahId > 1) {
                            OutlinedButton(
                                onClick = { onNavigateToSurah(viewModel.surahId - 1) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Surah Sebelumnya")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (viewModel.surahId < 114) {
                            Button(
                                onClick = { onNavigateToSurah(viewModel.surahId + 1) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Surah Berikutnya")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
