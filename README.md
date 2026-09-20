# Al-Qur'an Offline (Android)

[![Build Al-Qur'an Offline APK](https://github.com/KyuuX444/alquran-offline/actions/workflows/build.yml/badge.svg)](https://github.com/KyuuX444/alquran-offline/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-emerald.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-green.svg)](https://developer.android.com/jetpack/compose)
[![Offline First](https://img.shields.io/badge/Offline-100%25%20No%20Internet-success.svg)](#)

Aplikasi Android modern, ringan, dan bersih untuk membaca Al-Qur’an secara lengkap **30 Juz dan 114 Surah (6.236 Ayat)**. Didesain secara murni **100% Offline First** tanpa membutuhkan koneksi internet, backend/server, API online, maupun proses login/registrasi akun.

---

## 📱 Tangkapan Layar (Screenshots)

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│ Al-Qur'an Offline       │  │ Al-Fatihah (7 Ayat)     │  │ Pengaturan Tampilan     │
│ ─────────────────────── │  │ ─────────────────────── │  │ ─────────────────────── │
│ [ Terakhir Dibaca ]     │  │ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ │  │ [ Pratinjau Teks ]      │
│ Al-Baqarah — Ayat 25    │  │                         │  │                         │
│ [▶ Lanjut Baca]         │  │ 1. Dengan nama Allah... │  │ Ukuran Font Arab: 28sp  │
│                         │  │ [★] [⧉] [➦]             │  │ [───●─────────────]     │
│ Menu Utama:             │  │                         │  │                         │
│ [Surah]   [Juz]         │  │ ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ │  │ Ukuran Terjemahan: 15sp │
│ [Bookmark][Pencarian]   │  │ 2. Segala puji bagi...  │  │ [──────●──────────]     │
│ [Setting] [Privasi]     │  │                         │  │ [✓] Tampilkan Terjemahan│
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## ✨ Fitur Utama

- **Lengkap 30 Juz & 114 Surah**: Seluruh 6.236 ayat termuat langsung di dalam aplikasi (disimpan lokal).
- **100% Offline & Aman**: Tidak ada `INTERNET permission` di dalam `AndroidManifest.xml`. Aman digunakan dalam mode pesawat, tanpa sinyal, atau tanpa kuota.
- **Lanjut Baca (Last Read)**: Otomatis mencatat posisi ayat terakhir yang sedang dibaca dan dapat langsung dibuka kembali melalui tombol "Lanjut Baca".
- **Daftar Surah & Juz**: Navigasi cepat melalui 114 Surah (dilengkapi arti dan Makkiyah/Madaniyah) serta 30 Juz lengkap dengan batasan ayat awal dan akhir.
- **Pencarian Cepat (Fast Offline Search)**: Cari surah berdasarkan nama Latin, nama Arab, nomor surah, potongan teks Arab, maupun kata dalam terjemahan bahasa Indonesia dengan indexing database lokal.
- **Bookmark & Catatan Ayat**: Simpan ayat-ayat favorit ke bookmark lokal dan buka kembali kapan saja.
- **Fitur Reader Lengkap**:
  - Salin teks ayat (teks Arab, transliterasi, dan terjemahan).
  - Bagikan ayat ke media sosial atau aplikasi perpesanan.
  - Tampilan Basmalah elegan di setiap awal surah (kecuali Surah At-Tawbah).
- **Kustomisasi Tampilan**:
  - Pengaturan ukuran font Arab (20sp – 44sp).
  - Pengaturan ukuran font terjemahan (12sp – 24sp).
  - Toggle tampilkan/sembunyikan terjemahan.
  - Pilihan tema: **Mode Terang**, **Mode Gelap**, atau **Ikuti Sistem**.
- **Jaminan Privasi Penuh**: Tanpa login, tanpa analitik, tanpa pengumpulan data pribadi.

---

## 🛠️ Arsitektur & Teknologi

Aplikasi ini dibangun menggunakan arsitektur modern Android yang modular, bersih, dan mudah dipelihara:

- **Bahasa**: Kotlin (1.9.22)
- **UI Toolkit**: Jetpack Compose dengan Material 3
- **Database Lokal**: Room Database (SQLite pre-populated `quran.db` berindeks tinggi)
- **Penyimpanan Pengaturan**: AndroidX DataStore Preferences
- **Asinkron & Reaktif**: Kotlin Coroutines & Flow
- **Navigasi**: Jetpack Navigation Compose
- **Min SDK**: API 24 (Android 7.0 Nougat) — mencakup >95% perangkat Android aktif.
- **Target SDK**: API 34 (Android 14)
- **Build System**: Gradle 8.5 dengan Kotlin DSL (`build.gradle.kts`)

---

## 📂 Struktur Repositori

```
Alquran/
├── .github/
│   └── workflows/
│       ├── build.yml                 # CI: Validasi dataset, unit tests, dan build debug APK
│       └── release.yml               # CD: Release signed APK otomatis saat push Git Tag (v*)
├── app/
│   ├── build.gradle.kts              # Konfigurasi modul aplikasi
│   ├── proguard-rules.pro            # Optimasi dan aturan ProGuard/R8
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Manifest tanpa INTERNET permission
│       │   ├── assets/
│       │   │   ├── quran.db          # Database SQLite Al-Qur'an 30 Juz (3.4 MB)
│       │   │   └── quran_metadata.json # Metadata surah, juz, dan SHA-256 checksum
│       │   ├── java/com/alquran/offline/
│       │   │   ├── QuranApplication.kt
│       │   │   ├── data/
│       │   │   │   ├── local/        # Room Database, DAOs, Entities
│       │   │   │   ├── preferences/  # DataStore User Preferences
│       │   │   │   └── repository/   # QuranRepository & QuranRepositoryImpl
│       │   │   ├── model/            # Domain models (Surah, Ayah, Juz, Bookmark, LastRead)
│       │   │   └── ui/               # Jetpack Compose UI (Screens, Theme, Components, Navigation)
│       │   └── res/                  # Vector icons, colors, strings, adaptive launcher
│       └── test/java/com/alquran/offline/ # Unit tests (Dataset integrity, Search, Repository)
├── scripts/
│   ├── build_and_validate_dataset.py # Generator database dari dataset Tanzil & Kemenag
│   └── validate_dataset.py           # Validasi integritas ketat (114 surah, 6.236 ayat)
├── build.gradle.kts                  # Root Gradle build script
├── settings.gradle.kts               # Gradle settings
├── gradle.properties                 # Konfigurasi memori JVM & AndroidX
└── gradlew / gradlew.bat             # Gradle wrapper executable
```

---

## 🔍 Integritas & Validasi Dataset

Integritas teks kitab suci Al-Qur'an adalah prioritas mutlak. Repositori ini dilengkapi script validasi otomatis:

```bash
python3 scripts/validate_dataset.py
```

Pemeriksaan mencakup:
1. **Jumlah Surah**: Wajib tepat 114 surah (berurutan 1 sampai 114).
2. **Jumlah Ayat**: Wajib tepat 6.236 ayat tanpa ada yang tertinggal atau berlebih.
3. **Urutan Ayat**: Setiap surah diuji urutan ayatnya 1..N tanpa celah (zero gaps) dan tanpa duplikasi.
4. **Validasi Karakter**: Memastikan teks Arab Uthmani dan terjemahan utuh dalam UTF-8 tanpa karakter rusak (`\ufffd`).
5. **Cakupan Juz**: Memastikan ke-30 Juz terpetakan secara sempurna.
6. **Checksum SHA-256**: Memastikan database `quran.db` tidak termodifikasi secara tidak sengaja.

> **Catatan Penting**: Task `validateDataset` terintegrasi langsung ke dalam Gradle pre-build. Jika data mengalami anomali, proses kompilasi APK akan otomatis digagalkan.

---

## 🚀 Build Otomatis via GitHub Actions

Anda tidak perlu menginstall Android Studio atau membebani VPS/komputer Anda untuk mengompilasi APK. Cukup push repositori ini ke GitHub:

### 1. Build Debug APK
- Workflow `.github/workflows/build.yml` berjalan otomatis saat ada `push` atau `pull request` ke branch `main`/`master`, atau melalui pemicu manual **Run workflow**.
- Output APK otomatis diunggah ke bagian **Artifacts** dengan nama `alquran-offline-debug.apk`.

### 2. Build Signed Release APK
Untuk merilis versi resmi ber-tag:
1. Buat tag baru, misalnya `v1.0.0`:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. Workflow `.github/workflows/release.yml` akan berjalan, menandatangani APK (jika secret tersedia), dan otomatis mempublikasikannya ke halaman **Releases** di GitHub repository Anda.

#### Menyiapkan Release Signing (GitHub Secrets):
Tambahkan Secrets berikut di menu **Settings > Secrets and variables > Actions**:
- `KEYSTORE_BASE64`: File keystore `.jks` yang di-encode ke format base64 (`base64 -w 0 my-release-key.jks`).
- `KEYSTORE_PASSWORD`: Password keystore Anda.
- `KEY_ALIAS`: Alias key yang Anda gunakan.
- `KEY_PASSWORD`: Password untuk alias key tersebut.

*Jika secret tidak diatur, workflow akan otomatis membuat build debug/unsigned release tanpa membuat proses CI gagal.*

---

## 💻 Cara Menjalankan & Build Secara Lokal

Jika Anda memiliki JDK 17 dan Android SDK di komputer lokal:

1. **Clone repository**:
   ```bash
   git clone https://github.com/KyuuX444/alquran-offline.git
   cd alquran-offline
   ```

2. **Validasi dataset**:
   ```bash
   python3 scripts/validate_dataset.py
   ```

3. **Jalankan Unit Test**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. **Kompilasi Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   File APK akan tersedia di:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📜 Lisensi & Atribusi Dataset

1. **Teks Al-Qur'an (Rasm Utsmani)**:
   - Sumber: [Tanzil Project](https://tanzil.net)
   - Lisensi: [Creative Commons Attribution 3.0 Unported License](https://creativecommons.org/licenses/by/3.0/)
   - Teks Utsmani telah melalui proses verifikasi dan standardisasi internasional.
2. **Terjemahan Bahasa Indonesia**:
   - Sumber: Kementerian Agama Republik Indonesia (Kemenag RI).
   - Digunakan secara terbuka untuk tujuan edukasi dan kemaslahatan umat.
3. **Kode Sumber Aplikasi**:
   - Kode aplikasi ini dilisensikan di bawah [MIT License](LICENSE).
