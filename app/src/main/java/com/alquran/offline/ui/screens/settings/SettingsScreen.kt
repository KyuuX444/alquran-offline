package com.alquran.offline.ui.screens.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alquran.offline.model.ThemeMode
import com.alquran.offline.ui.components.AppTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val context = LocalContext.current
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()
    val translationFontSize by viewModel.translationFontSize.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationHour by viewModel.notificationHour.collectAsState()
    val notificationMinute by viewModel.notificationMinute.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }

    // Notification Permission Launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setNotificationEnabled(true)
            scope.launch {
                snackbarHostState.showSnackbar("Notifikasi hadits harian diaktifkan")
            }
        } else {
            viewModel.setNotificationEnabled(false)
            scope.launch {
                snackbarHostState.showSnackbar("Izin notifikasi tidak diberikan oleh sistem")
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Pengaturan",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Live Preview Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "PRATINJAU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontFamily = FontFamily.Serif,
                        fontSize = arabicFontSize.sp,
                        lineHeight = (arabicFontSize * 2.0f).sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDirection = TextDirection.Rtl
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (showTranslation) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.",
                            fontSize = translationFontSize.sp,
                            lineHeight = (translationFontSize * 1.5f).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // SECTION: TAMPILAN
            item {
                SectionHeader("TAMPILAN")

                val themeLabel = when (themeMode) {
                    ThemeMode.SYSTEM -> "Mengikuti sistem"
                    ThemeMode.LIGHT -> "Mode Terang"
                    ThemeMode.DARK -> "Mode Gelap"
                }
                SettingsPreferenceRow(
                    title = "Tema",
                    subtitle = themeLabel,
                    onClick = { showThemeDialog = true }
                )

                // Arabic font size slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ukuran teks Arab",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${arabicFontSize.toInt()} sp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = arabicFontSize,
                        onValueChange = viewModel::setArabicFontSize,
                        valueRange = 20f..44f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Translation font size slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ukuran teks terjemahan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${translationFontSize.toInt()} sp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = translationFontSize,
                        onValueChange = viewModel::setTranslationFontSize,
                        valueRange = 12f..24f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Translation switch
                SettingsSwitchRow(
                    title = "Tampilkan terjemahan",
                    subtitle = "Bahasa Indonesia (Kemenag RI)",
                    checked = showTranslation,
                    onCheckedChange = viewModel::setShowTranslation
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // SECTION: NOTIFIKASI
            item {
                SectionHeader("NOTIFIKASI")

                SettingsSwitchRow(
                    title = "Hadits Harian",
                    subtitle = "Pengingat hadits pilihan setiap hari (100% offline)",
                    checked = notificationEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.setNotificationEnabled(true)
                                } else {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                viewModel.setNotificationEnabled(true)
                            }
                        } else {
                            viewModel.setNotificationEnabled(false)
                        }
                    }
                )

                if (notificationEnabled) {
                    val timeFormatted = String.format("%02d:%02d", notificationHour, notificationMinute)
                    SettingsPreferenceRow(
                        title = "Waktu notifikasi",
                        subtitle = timeFormatted,
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    viewModel.setNotificationTime(h, m)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Waktu notifikasi diubah ke ${String.format("%02d:%02d", h, m)}")
                                    }
                                },
                                notificationHour,
                                notificationMinute,
                                true
                            ).show()
                        }
                    )
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // SECTION: DATA & PRIVASI
            item {
                SectionHeader("DATA & PRIVASI")

                SettingsPreferenceRow(
                    title = "Reset posisi terakhir dibaca",
                    subtitle = "Kembalikan penanda ke Surah Al-Fatihah: 1",
                    onClick = {
                        viewModel.resetLastRead()
                        scope.launch {
                            snackbarHostState.showSnackbar("Posisi bacaan direset ke Surah Al-Fatihah: 1")
                        }
                    }
                )

                SettingsPreferenceRow(
                    title = "Tentang & Kebijakan Privasi",
                    subtitle = "100% Offline · Tanpa analitik atau pelacakan data",
                    onClick = onPrivacyClick
                )

                SettingsPreferenceRow(
                    title = "Versi Aplikasi",
                    subtitle = "1.0 (Build 1) · Full Offline",
                    onClick = onPrivacyClick
                )
            }
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Pilih Tema",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    ThemeDialogOption(
                        label = "Mengikuti sistem",
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                    ThemeDialogOption(
                        label = "Mode Terang",
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                    )
                    ThemeDialogOption(
                        label = "Mode Gelap",
                        selected = themeMode == ThemeMode.DARK,
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.DARK)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsPreferenceRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun ThemeDialogOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
