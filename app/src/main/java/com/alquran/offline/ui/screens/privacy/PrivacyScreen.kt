package com.alquran.offline.ui.screens.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.offline.ui.components.AppTopBar

@Composable
fun PrivacyScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Kebijakan Privasi & Atribusi",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Offline Security Note
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "100% OFFLINE FIRST",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Aplikasi ini dirancang untuk beroperasi sepenuhnya tanpa koneksi internet. Tidak ada izin akses jaringan (INTERNET permission) yang diminta oleh aplikasi ini ke sistem operasi Android. Seluruh data teks Al-Qur'an dan Hadits telah disertakan langsung di dalam paket instalasi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }

            // Privacy Guarantees
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "JAMINAN PRIVASI",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    PrivacyItem(
                        title = "Tanpa Akun & Registrasi",
                        desc = "Aplikasi dapat langsung digunakan tanpa mendaftar atau login."
                    )
                    PrivacyItem(
                        title = "Tanpa Pelacakan / Analytics",
                        desc = "Tidak ada pelacakan penggunaan, pengumpulan data pribadi, maupun log aktivitas."
                    )
                    PrivacyItem(
                        title = "Penyimpanan Lokal Sepenuhnya",
                        desc = "Penanda baca (bookmark), riwayat terakhir dibaca, dan preferensi tampilan hanya tersimpan di perangkat Anda."
                    )
                    PrivacyItem(
                        title = "Tanpa Izin Sensitif",
                        desc = "Aplikasi tidak mengakses kontak, lokasi, kamera, mikrofon, atau penyimpanan eksternal."
                    )
                }
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }

            // Data Attribution
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SUMBER & ATRIBUSI DATASET",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "1. Teks Al-Qur'an (Rasm Utsmani):\nBersumber dari Tanzil Project (tanzil.net), terverifikasi dengan standar mushaf Utsmani internasional dan berlisensi Creative Commons Attribution 3.0.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. Terjemahan Bahasa Indonesia:\nBersumber dari Kementerian Agama Republik Indonesia (Kemenag RI), digunakan secara terbuka untuk kepentingan literasi dan dakwah.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3. Hadits Arbain An-Nawawi:\n42 Hadits pilihan karya Imam An-Nawawi beserta terjemahan Bahasa Indonesia yang telah diverifikasi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyItem(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
