<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_playstore.png" width="96" height="96" alt="Al-Qur'an Offline Logo" style="border-radius: 20px;">
</p>

<h1 align="center">Al-Qur'an Offline</h1>

<p align="center">
  Aplikasi Al-Qur'an dan Hadits untuk Android yang dirancang murni <i>offline-first</i>.<br>
  Fokus pada ketenangan membaca, tipografi yang nyaman, dan privasi penuh tanpa koneksi internet.
</p>

<p align="center">
  <a href="https://github.com/KyuuX444/alquran-offline/releases"><img src="https://img.shields.io/badge/Download-APK%20v1.0-1E5638?style=for-the-badge&logo=android&logoColor=white" alt="Download APK"></a>
  <a href="https://github.com/KyuuX444/alquran-offline"><img src="https://img.shields.io/badge/GitHub-Repository-24292e?style=for-the-badge&logo=github&logoColor=white" alt="GitHub Repo"></a>
  <a href="https://github.com/KyuuX444/alquran-offline/issues"><img src="https://img.shields.io/badge/Issues-Report-grey?style=for-the-badge&logo=github" alt="Issues"></a>
</p>

<p align="center">
  <a href="https://github.com/KyuuX444/alquran-offline/actions/workflows/build.yml"><img src="https://github.com/KyuuX444/alquran-offline/actions/workflows/build.yml/badge.svg" alt="Build Status"></a>
  <img src="https://img.shields.io/badge/Android-5.0+_(API_21--34)-3DDC84?logo=android&logoColor=white" alt="Android Support">
  <img src="https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-2E7D32.svg" alt="License: MIT"></a>
</p>

---

## Deskripsi

**Al-Qur'an Offline** adalah aplikasi Android open-source yang memuat seluruh mushaf Al-Qur'an (114 Surah, 30 Juz, 6.236 Ayat) serta 42 Hadits Arbain An-Nawawi secara mandiri di dalam perangkat.

Aplikasi ini tidak memiliki izin akses jaringan (`android.permission.INTERNET`). Tidak ada server pengumpul data, tidak ada pelacakan analitik, dan tidak ada sistem login. Seluruh data teks, terjemahan, penanda baca (bookmark), dan preferensi tersimpan di penyimpanan lokal perangkat Anda.

---

## Features

- 📖 **Al-Qur'an Lengkap** — 114 Surah dan 30 Juz (6.236 ayat) dengan teks Utsmani terverifikasi dan terjemahan bahasa Indonesia Kemenag RI.
- 📚 **Hadits Offline** — 42 Hadits Arbain An-Nawawi lengkap dengan teks Arab, terjemahan, dan perawi.
- 🔎 **Pencarian Lokal** — Temukan surah, potongan ayat Arab, maupun kata dalam terjemahan secara instan melalui indexing database internal.
- 🔖 **Bookmark Ayat** — Simpan ayat-ayat penting ke daftar penanda lokal dengan sekali sentuh.
- ↩️ **Lanjut Baca (Last Read)** — Melanjutkan tilawah langsung dari ayat dan surah terakhir yang dibuka.
- 🌙 **Mode Tampilan** — Dukungan tema Terang (Warm Paper), Gelap (Warm Charcoal), serta mengikuti pengaturan sistem.
- 🔔 **Hadits Harian** — Pengingat notifikasi hadits harian (Senin–Minggu) tanpa membutuhkan akses internet atau server push.
- ⚡ **App Shortcuts** — Akses instan dari homescreen ke Lanjut Baca, Surah, Juz, dan Hadits.
- 📡 **100% Offline** — Nol koneksi internet, tanpa API eksternal, dan hemat konsumsi daya.

---

## Screenshots

<p align="center">
  <i>Pratinjau antarmuka aplikasi (tampilan mode terang dan mode gelap):</i>
</p>

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│ Beranda                 │  │ Al-Baqarah (Ayat 255)   │  │ Pengaturan              │
├─────────────────────────┤  ├─────────────────────────┤  ├─────────────────────────┤
│ Lanjut membaca          │  │ ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ    │  │ TAMPILAN                │
│ Al-Baqarah : Ayat 255   │  │ ٱلْحَىُّ ٱلْقَيُّومُ...         │  │ Ukuran Font Arab: 28sp   │
│ [ Lanjutkan Tilawah ]   │  │                         │  │ Ukuran Terjemahan: 15sp  │
│ ─────────────────────── │  │ Allah, tidak ada tuhan  │  │ Tema: Ikuti Sistem       │
│ Al-Qur'an    (114 Surah)│  │ selain Dia. Yang Maha   │  │ ─────────────────────── │
│ Hadits Arbain (42 Hadits│  │ Hidup...                │  │ NOTIFIKASI               │
│ Penanda Baca (Bookmark) │  │                         │  │ Hadits Harian: 06:00     │
│ ─────────────────────── │  │ [ Bookmark ]  [ Salin ] │  │ ─────────────────────── │
│ Hadits Hari Ini:        │  │ [ Bagikan ]             │  │ DATA & PRIVASI           │
│ "Innamal a'malu binniyat│  │                         │  │ 100% Offline, Tanpa Izin │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

> **Catatan:** Screenshot tangkapan layar perangkat nyata akan diperbarui secara bertahap pada direktori `docs/screenshots/`.

---

## Offline First

Arsitektur aplikasi ini mengisolasi seluruh alur data dari jaringan luar:

```
                  Koneksi Internet
                         │
                         ▼
                   [ TIDAK ADA ]
                         │
  ┌──────────────────────┴──────────────────────┐
  │              PERANGKAT ANDROID              │
  │                                             │
  │  Teks Al-Qur'an (SQLite/Room) ───────┐      │
  │  Hadits Arbain (SQLite/Room) ────────┼──┐   │
  │  Riwayat Terakhir Baca (DataStore) ──┤  │   │
  │  Bookmark Ayat (Room DB) ────────────┤  ▼   │
  │  Pengaturan Tampilan (DataStore) ────┴──► Jetpack Compose UI
  └─────────────────────────────────────────────┘
```

Tidak ada permintaan HTTP, socket, atau sinkronisasi background ke server pihak ketiga.

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Compose">
  <img src="https://img.shields.io/badge/Android_SDK-minSdk_21_%7C_targetSdk_34-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android SDK">
  <img src="https://img.shields.io/badge/Room_DB-SQLite_3.4MB-003B57?style=flat-square&logo=sqlite&logoColor=white" alt="Room">
  <img src="https://img.shields.io/badge/DataStore-Preferences-00599C?style=flat-square" alt="DataStore">
  <img src="https://img.shields.io/badge/Gradle-8.5-02303A?style=flat-square&logo=gradle&logoColor=white" alt="Gradle">
  <img src="https://img.shields.io/badge/GitHub_Actions-CI%2FCD-2088FF?style=flat-square&logo=githubactions&logoColor=white" alt="GitHub Actions">
</p>

- **Language:** [Kotlin](https://kotlinlang.org/) (1.9.22) dengan Coroutines & StateFlow
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) dengan [Material Design 3](https://m3.material.io/)
- **Local Database:** Room Persistence Library di atas SQLite berindeks
- **Preferences:** AndroidX DataStore Preferences (pengganti SharedPreferences)
- **Background Tasks:** Android AlarmManager (Exact & Idle fallback) + BroadcastReceiver
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) dengan R8 minification

---

## Data Sources

Dataset Al-Qur'an dan Hadits telah melalui pemeriksaan integritas otomatis (114 Surah, 6.236 Ayat, 42 Hadits) sebelum proses build:

1. **Teks Al-Qur'an (Rasm Utsmani)**  
   Bersumber dari [Tanzil Project](https://tanzil.net), terverifikasi dengan standar mushaf Utsmani internasional.  
   *Lisensi:* [Creative Commons Attribution 3.0 Unported (CC BY 3.0)](https://creativecommons.org/licenses/by/3.0/).

2. **Terjemahan Bahasa Indonesia**  
   Bersumber dari Kementerian Agama Republik Indonesia (Kemenag RI), digunakan untuk kepentingan literasi dan dakwah Islam secara terbuka.

3. **Hadits Arbain An-Nawawi**  
   Kompilasi 42 hadits pilihan karya Imam An-Nawawi dengan sanad, matan Arab, dan terjemahan bahasa Indonesia yang telah terverifikasi.

---

## Installation

### Unduh APK Siap Pakai
1. Kunjungi halaman [Releases](https://github.com/KyuuX444/alquran-offline/releases).
2. Unduh file `alquran-offline-v1.apk` dari rilis terbaru.
3. Buka file APK di perangkat Android Anda (izinkan instalasi dari sumber tidak dikenal jika diminta).

---

## Build from Source

Jika Anda ingin mengompilasi aplikasi ini sendiri:

```bash
# 1. Clone repository
git clone https://github.com/KyuuX444/alquran-offline.git
cd alquran-offline

# 2. Verifikasi integritas dataset
python3 scripts/validate_dataset.py
python3 scripts/validate_hadith_dataset.py

# 3. Jalankan unit test
./gradlew testDebugUnitTest

# 4. Buat file APK Debug
./gradlew assembleDebug
```

File APK hasil build akan tersedia pada direktori:  
`app/build/outputs/apk/debug/app-debug.apk`

---

## Build with GitHub Actions

Repository ini dilengkapi alur kerja otomasi berbasis [GitHub Actions](.github/workflows):

- **Continuous Integration (`build.yml`)**  
  Berjalan pada setiap `push` dan `pull request` ke branch `main`. Memverifikasi checksum dataset, menjalankan unit test, linter Android, dan memverifikasi kompilasi aplikasi.
- **Automated Release (`release.yml`)**  
  Berjalan otomatis ketika Git Tag berawalan `v*` (contoh: `v1.0.0`) di-push. Menandatangani APK menggunakan skema v1 + v2 + v3, mengoptimalkan biner via R8, dan mempublikasikan paket rilis ke GitHub Releases.

---

## Project Structure

```
alquran-offline/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # Konfigurasi aplikasi (tanpa INTERNET)
│   │   ├── assets/
│   │   │   ├── quran.db                 # Database SQLite Al-Qur'an 30 Juz
│   │   │   └── quran_metadata.json      # Metadata checksum & struktur surah
│   │   ├── java/com/alquran/offline/
│   │   │   ├── data/                    # Room DB, Entity, DAO, Preferences
│   │   │   ├── model/                   # Model domain Surah, Ayah, Hadith
│   │   │   ├── notification/            # Scheduler notifikasi lokal
│   │   │   ├── receiver/                # AlarmReceiver & BootReceiver
│   │   │   └── ui/                      # Jetpack Compose Screens & Components
│   │   └── res/                         # Asset visual, XML shortcuts, tema
│   └── src/test/java/com/alquran/       # Unit tests (Room, Nav, Compatibility)
├── scripts/
│   ├── validate_dataset.py              # Validasi integritas Al-Qur'an
│   └── validate_hadith_dataset.py       # Validasi integritas Hadits Arbain
├── .github/workflows/                   # CI/CD Workflows
└── build.gradle.kts                     # Konfigurasi root build
```

---

## Privacy

Aplikasi ini mengedepankan privasi secara fundamental:

- **Tanpa Akun:** Tidak ada registrasi, login, atau permintaan profil pengguna.
- **Tanpa Izin Jaringan:** Tidak meminta `android.permission.INTERNET`. Secara sistemik, sistem operasi Android memblokir aplikasi ini dari komunikasi jaringan apapun.
- **Tanpa Analytics / Telemetry:** Bebas dari SDK pelacak pihak ketiga (Firebase, Google Analytics, dsb).
- **Data Tersimpan Lokal:** Bookmark, riwayat baca, dan preferensi tersimpan aman di direktori internal aplikasi pada memori perangkat.

---

## Design Philosophy

> *« Dibuat untuk membaca, bukan untuk memamerkan UI. »*

- **Typography First:** Teks Arab Utsmani disajikan dengan ruang vertikal yang lega (line-height proporsional) agar tanda harakat mudah dibaca tanpa melelahkan mata.
- **Content Centric:** Menghilangkan kartu tebal, efek neon, dan gradien dekoratif yang mengalihkan perhatian dari inti ayat.
- **Minimalist & Native:** Mengikuti pola antarmuka native Android yang bersih, tenang, dan terintegrasi mulus dengan tema sistem.
- **Ringan & Cepat:** Transisi instan antar layar tanpa hambatan pemuatan jaringan.

---

## Contributing

Kontribusi berupa perbaikan bug, penyempurnaan tipografi, atau optimasi kinerja sangat diapresiasi:

1. Lakukan **Fork** pada repositori ini.
2. Buat branch fitur baru (`git checkout -b feature/penyempurnaan-fitur`).
3. Terapkan perubahan dan pastikan unit test lolos (`./gradlew testDebugUnitTest`).
4. Lakukan commit perubahan (`git commit -m 'feat: deskripsi penyempurnaan'`).
5. Push ke branch Anda (`git push origin feature/penyempurnaan-fitur`).
6. Buka **Pull Request** ke branch `main`.

---

## Thanks to

Aplikasi ini terwujud berkat karya dan dedikasi komunitas open-source:

- **[Tanzil Project](https://tanzil.net)** atas dataset teks Al-Qur'an Rasm Utsmani berstandar internasional yang teruji akurasinya.
- **Kementerian Agama Republik Indonesia** atas penyediaan teks terjemahan Al-Qur'an bahasa Indonesia.
- Komunitas pengembang Android & Kotlin atas ekosistem Jetpack Compose yang luar biasa.

---

## License

- **Source Code:** Dilisensikan di bawah [MIT License](LICENSE).
- **Teks Al-Qur'an:** [Creative Commons Attribution 3.0 Unported](https://creativecommons.org/licenses/by/3.0/) (Tanzil Project).
- **Terjemahan & Hadits:** Digunakan secara terbuka untuk kepentingan literasi dan dakwah Islam.

---

## Author

Dikembangkan dan dipelihara oleh **Kyuu** ([@KyuuX444](https://github.com/KyuuX444)).

---

<p align="center">
  <sub>Al-Qur'an Offline · Android · 100% Offline First · Open Source</sub>
</p>
