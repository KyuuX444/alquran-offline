package com.alquran.offline.ui.screens.asmaul_husna

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.offline.model.AsmaulHusna
import com.alquran.offline.ui.components.AppTopBar

@Composable
fun AsmaulHusnaScreen(
    viewModel: AsmaulHusnaViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val asmaulHusnaList by viewModel.asmaulHusnaList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    fun copyToClipboard(item: AsmaulHusna) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val text = "${item.id}. ${item.nameLatin} (${item.nameAr})\nArtinya: ${item.meaningId}"
        val clip = ClipData.newPlainText("Asmaul Husna", text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "${item.nameLatin} disalin", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Asmaul Husna",
                subtitle = "99 Nama Indah Allah",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Beranda"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Cari nama atau arti...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menampilkan ${asmaulHusnaList.size} dari 99 Nama",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ketuk kartu untuk menyalin",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 2-Column Responsive Grid of Asmaul Husna Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = asmaulHusnaList,
                    key = { it.id }
                ) { item ->
                    AsmaulHusnaCard(
                        item = item,
                        onClick = { copyToClipboard(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun AsmaulHusnaCard(
    item: AsmaulHusna,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Number badge on top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${item.id}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Arabic text in vocalized font
            Text(
                text = item.nameAr,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                lineHeight = 36.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    textDirection = TextDirection.Rtl
                ),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Latin transliteration
            Text(
                text = item.nameLatin,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Meaning in Indonesian
            Text(
                text = item.meaningId,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}
