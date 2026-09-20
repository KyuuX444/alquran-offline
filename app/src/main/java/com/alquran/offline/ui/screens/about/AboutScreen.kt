package com.alquran.offline.ui.screens.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquran.offline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tentang Aplikasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section: App Icon, Name, Subtitle, Version
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_playstore),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(22.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Al-Qur'an Offline",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Baca dan pelajari Al-Qur'an, Hadist, doa, dan bacaan ibadah tanpa koneksi internet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Version 1.0",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // About App Section
            item {
                AboutSectionCard(title = "Tentang Aplikasi", icon = Icons.Default.Info) {
                    Text(
                        text = "Al-Qur'an Offline adalah aplikasi untuk membantu membaca dan mempelajari Al-Qur'an, Hadist, doa, serta bacaan ibadah kapan saja tanpa membutuhkan koneksi internet. Seluruh data Al-Qur'an 30 Juz, puluhan ribu hadits shahih, bacaan sholat, dan doa harian tersimpan langsung di dalam perangkat dengan privasi penuh dan tanpa izin internet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            // Features Summary (Compact List)
            item {
                AboutSectionCard(title = "Fitur Aplikasi", icon = Icons.Default.MenuBook) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeatureCompactRow("Al-Qur'an", "114 Surah • 30 Juz teks Utsmani lengkap")
                        FeatureCompactRow("Hadist", "Koleksi Hadist offline 38.100+ Kutubut Tis'ah")
                        FeatureCompactRow("Hadist Harian", "Hadist pilihan yang dapat dibuka langsung")
                        FeatureCompactRow("Bacaan Sholat", "Bacaan sholat lengkap bersumber dari hadits shahih")
                        FeatureCompactRow("Doa Harian", "Kumpulan doa untuk berbagai aktivitas")
                        FeatureCompactRow("Bookmark", "Simpan ayat/Hadist favorit")
                        FeatureCompactRow("Last Read", "Lanjutkan bacaan terakhir secara otomatis")
                        FeatureCompactRow("Pencarian", "Cari ayat dan Hadist secara offline")
                        FeatureCompactRow("Mode Gelap", "Tampilan nyaman untuk membaca malam hari")
                        FeatureCompactRow("Notifikasi", "Pengingat Hadist Harian mandiri")
                    }
                }
            }

            // Developer Section
            item {
                AboutSectionCard(title = "Dikembangkan oleh", icon = Icons.Default.Person) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Kyuryn (S.D.Y)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Independent developer yang membangun aplikasi, tools, dan berbagai project open-source.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Tech Stack Section
            item {
                AboutSectionCard(title = "Tech Stack", icon = Icons.Default.Code) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Teknologi yang Digunakan Aplikasi",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Kotlin 1.9 & Coroutines / StateFlow\n• Jetpack Compose & Material 3 UI\n• Android SDK (minSdk 21, targetSdk 34)\n• Room Persistence Library (Indexed SQLite)\n• AndroidX DataStore Preferences\n• 100% Offline Architecture (0 Network Permission)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Text(
                            text = "Teknologi yang Dikuasai Developer",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Go • Kotlin • Java • PHP • JavaScript • TypeScript • C • C++ • pecut eyay • Python • dan lainnya",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Contact / Social ("Terhubung")
            item {
                AboutSectionCard(title = "Terhubung", icon = Icons.Default.Share) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // WhatsApp Channel 1
                        ContactItemRow(
                            label = "WhatsApp Channel 1",
                            subtitle = "Informasi & pembaruan project",
                            onClick = {
                                openUrlSafely(context, "https://whatsapp.com/channel/0029VbDO8tI2phHLTSN2ed0U")
                            }
                        )

                        // WhatsApp Channel 2
                        ContactItemRow(
                            label = "WhatsApp Channel 2",
                            subtitle = "Komunitas & channel rilis",
                            onClick = {
                                openUrlSafely(context, "https://whatsapp.com/channel/0029VbCKm3I5EjxsUbvmVf33")
                            }
                        )

                        // Telegram 1
                        ContactItemRow(
                            label = "Telegram (kyuumasihcwo)",
                            subtitle = "t.me/kyuumasihcwo",
                            onClick = {
                                openUrlSafely(context, "https://t.me/kyuumasihcwo")
                            }
                        )

                        // Telegram 2
                        ContactItemRow(
                            label = "Telegram (kyunotdev)",
                            subtitle = "t.me/kyunotdev",
                            onClick = {
                                openUrlSafely(context, "https://t.me/kyunotdev")
                            }
                        )
                    }
                }
            }

            // Source & Attribution Section
            item {
                AboutSectionCard(title = "Sumber & Kredit", icon = Icons.Default.VerifiedUser) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AttributionRow("Al-Qur'an (Rasm Utsmani)", "Tanzil Project (tanzil.net) - Lisensi CC BY 3.0")
                        AttributionRow("Terjemahan Indonesia", "Kementerian Agama Republik Indonesia (Kemenag RI)")
                        AttributionRow("Koleksi Hadits", "38.144 Hadits Kutubut Tis'ah & Kitab Arba'in An-Nawawi")
                        AttributionRow("Bacaan Sholat", "Kitab Shifat Shalat Nabi & Rujukan Hadits Shahih (Bukhari, Muslim, dll)")
                        AttributionRow("Doa Harian", "Hisnul Muslim (Sa'id bin Ali bin Wahf Al-Qahthani) & Kitab Al-Adzkar")
                        AttributionRow("Library / UI", "Android Jetpack, Jetpack Compose Material 3, SQLite")
                    }
                }
            }

            // License Section
            item {
                AboutSectionCard(title = "Lisensi", icon = Icons.Default.Info) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "MIT License",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Kode sumber aplikasi berlisensi MIT. Anda bebas mempelajari dan memodifikasi kode sumber dengan tetap mencantumkan hak cipta pengembang. Seluruh teks Al-Qur'an, Hadits, dan doa merupakan milik umat Islam dan disajikan secara terbuka untuk kepentingan ibadah serta dakwah.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Footer About
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Al-Qur'an Offline",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Made with care by Kyuryn (S.D.Y)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "© 2026 Kyuryn",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            content()
        }
    }
}

@Composable
fun FeatureCompactRow(name: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ContactItemRow(label: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Buka Link",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AttributionRow(title: String, source: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = source,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun openUrlSafely(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Throwable) {
        Toast.makeText(context, "Tidak dapat membuka tautan", Toast.LENGTH_SHORT).show()
    }
}
