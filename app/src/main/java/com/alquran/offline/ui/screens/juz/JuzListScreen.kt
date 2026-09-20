package com.alquran.offline.ui.screens.juz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alquran.offline.model.JuzInfo
import com.alquran.offline.ui.components.AppTopBar

@Composable
fun JuzListScreen(
    viewModel: JuzListViewModel,
    onBackClick: () -> Unit,
    onJuzClick: (Int, Int) -> Unit,
    onNavigateToSurah: (() -> Unit)? = null
) {
    val juzList = viewModel.allJuz

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Al-Qur'an",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Clean TabRow for Surah / Juz switcher
            if (onNavigateToSurah != null) {
                TabRow(
                    selectedTabIndex = 1,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[1]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }
                ) {
                    Tab(
                        selected = false,
                        onClick = { onNavigateToSurah() },
                        text = {
                            Text(
                                text = "Surah (114)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = true,
                        onClick = { },
                        text = {
                            Text(
                                text = "Juz (30)",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            // Juz Native List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = juzList,
                    key = { it.juzNumber }
                ) { juz ->
                    JuzListItem(
                        juz = juz,
                        onClick = {
                            onJuzClick(juz.startSurahId, juz.startVerse)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun JuzListItem(
    juz: JuzInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Juz Number (01 .. 30)
            Text(
                text = String.format("%02d", juz.juzNumber),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.width(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Juz ${juz.juzNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${juz.startSurahName} (${juz.startVerse}) — ${juz.endSurahName} (${juz.endVerse})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            modifier = Modifier.padding(start = 60.dp)
        )
    }
}
