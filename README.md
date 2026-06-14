**FundFlow**
  
  FundFlow adalah aplikasi keuangan berbasis mobile yang dirancang untuk membantu bendahara organisasi mengelola iuran, pemasukan, pengeluaran, dan laporan keuangan secara terstruktur, terpusat, dan real-time.

**Penjelasan Fitur**
- **Fitur Wajib**
  1. Minimal 6 Halaman (Screen): Terdiri dari Landing Page, Login/Register, Home, Iuran, Manajemen Anggota, Pemasukan, Pengeluaran, Laporan, dan Pengaturan.
  2. Recycle-able List & Detail Data: Menampilkan daftar transaksi dan anggota yang ringan saat di-scroll, dilengkapi bottom sheet untuk melihat detail data.
  3. Fitur BREAD Penuh: Mampu melakukan operasi Browse, Read, Edit, Add, dan Delete pada entitas Anggota, Iuran, Pemasukan, dan Pengeluaran.
  4. Fetching Data API: Menampilkan informasi Libur Nasional Mendatang secara real-time di halaman Home.
  5. Local Database (Offline-First): Menggunakan Room Database sebagai pusat penyimpanan utama agar aplikasi dapat beroperasi penuh tanpa koneksi internet.
     
- **Fitur Tambahan**
  1. Laporan & Export Otomatis: Menghasilkan rekapitulasi data keuangan dan menyediakan opsi Export ke format PDF dan Excel.
  2. Firebase Authentication: Sistem keamanan login terenkripsi menggunakan Email dan Password.
  3. Push Notification: Menggunakan Firebase Cloud Messaging (FCM) dan WorkManager untuk notifikasi pengingat kas.
  4. Personalisasi Tema & Bahasa: Mendukung perpindahan ke Mode Gelap (Dark Mode) dan pengaturan preferensi Bahasa (Indonesia/Inggris) yang disimpan secara lokal via DataStore.

**Cara Instlasi**
1. Patikan komputer sudah terinstal Android Studio
2. Buka terminal atau command prompt, lalu clone repository ini menggunakan perintah: git clone [https://github.com/Amanda-06/FundFlow.git]
3. Buka Android Studio, pilih menu File > Open, lalu arahkan ke folder hasil clone repository FundFlow.
4. Tunggu beberapa saat hingga proses sinkronisasi Gradle selesai mengunduh seluruh dependensi aplikasi.
5. Masukkan file konfigurasi **google-services.json** ke dalam direktori **app/** agar proyek dapat terhubung dalam layanan Firebase.

**Cara Menjalankan Aplikasi**
1. Siapkan perangkat Android, aktifkan mode USB Debugging di HP atau jalankan emulator bawaan Android Studio.
2. Pastikan perangkat target sudah terbaca dan terpilih pada menu  dropdown device di toolbar Android Studio.
3. Klik tombol Run atau gunakan shortcut keyboard **Shift + F10**.
4. Tunggu hingga proses build selesai. Jika tidak ada error, aplikasi FundFloe akan langsung terinstal dan terbuka di layar perangkat.

**Cara Instalasi dan Menjalankan Aplikasi (File APK)**
1. Unduh file instalasi (.apk) FundFlow yang telah disediakan.
2. Buka file .apk tersebut di perangkat smartphone Android.
3. Jika muncul peringatan keamanan, masuk ke pengaturan dan aktifkan izin instal dari sumber tidak dikenal.
4. Lanjutkan proses instalasi hingga selesai.
5. Setelah berhasil terinstal, ketuk ikon aplikasi FundFlow dilayar utama untuk mulai menggunakannya.

**Informasi API Publik yang digunakan**
Aplikasi ini melakukan fetching data ke pihak ketiga untuk mendapatkan informasi kalender libur nasional.
1. Nama API: Nager.Date API (Public Holiday API)
2. Endpoint: https://date.nager.at/api/v3/NextPublicHolidays/ID
3. Metode: GET
4. Data yang diambil: Nama hari libur (name), nama lokal (localName), dan tanggal libur (date) terdekat untuk wilayah Indonesia (ID).

**Informasi Teknologi & Arsitektur**
Aplikasi ini dikembangkan menggunakan pola arsitektur Clean Architecture + MVVM (Model-View-ViewModel) untuk memastikan skalabilitas dan keandalan performa
1. Kotlin: Bahasa pemrograman utama.
2. Jetpack Compose + Material 3: Perangkat khusus untuk membangun antarmuka aplikasi UI modern yang responsif dan deklaratif.
3. Hilt: Digunakan sebagai Dependency Injection untuk menyederhanakan struktur kode.
4. Room Database: Digunakan untuk penyimpanan data relasional lokal (konsep offline-first).
5. Firebase Services: Authentication untuk login, Firestore untuk cloud backup, dan Cloud Messaging (FCM) untuk notifikasi.
6. DataStore: Teknologi penyimpanan asinkron untuk preferensi pengaturan pengguna.
7. Retrofit & KotlinX Serialization: Retrofit sebagai HTTP Client untuk memanggil API publik, dan KotlinX Serialization untuk melakukan parsing data JSON.
8. PDF & Excel Generator: Menggunakan android-pdf-document dan Poi-Android.
9. Coil: Memuat aset gambar secara asinkron dengan cepat dan ringan.

**Struktur Folder Proyek**
Proyek ini disusun secara modular berdasarkan fitur (feature-based) dan lapisan arsitekturnya

com.example.fundflow/
├── core/
│   ├── database/
│   ├── datastore/
│   ├── di/
│   ├── firebase/
│   ├── network/
│   ├── util/
│   └── worker/
├── feature/
│   ├── anggota/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── auth/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── home/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── iuran/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── laporan/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── onboarding/
│   │   └── presentation/
│   ├── pemasukan/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── pengeluaran/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── profile/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   └── settings/
│       ├── data/
│       ├── domain/
│       └── presentation/
├── navigation/
├── ui/
│   ├── components/
│   └── theme/
├── res/
|   ├── values/
|    └── values-en/
└── xml


