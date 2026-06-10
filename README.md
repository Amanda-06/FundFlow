**FundFlow**
  
  FundFlow adalah aplikasi keuangan berbasis mobile yang dirancang untuk membantu bendahara organisasi mengelola iuran, pemasukan, pengeluaran, dan laporan keuangan secara terstruktur, terpusat, dan real-time.

**Penjelasan Fitur**
1. Dashboard Utama: Menampilkan ringkasan kondisi keuangan secara langsung, termasuk total saldo, total pemasukan, total pengeluaran, status iuran, informasi libur nasional, serta riwayat transaksi terbaru.
2. Manajemen Anggota: Memungkinkan pengguna untuk menambahkan, mengedit, dan menghapus data anggota yang terintegrasi langsung dengan sistem pembayaran iuran bulanan.
3. Pencatatan Iuran: Memantau dan mencatat pembayaran kas dari anggota berdasarkan periode yang ditentukan, lengkap dengan rincian nominal dan metode pembayaran.
4. Kelola Pemasukan dan Pengeluaran: Form untuk mencatat transaksi masuk seperti dana proker atau sponsorship dan keluar, dilengkapu kalkulasi nominal otomatis berdasarkan kuantitas dikali harga satuan.
5. Laporan Otomatis: Fitur untuk menghasilkan rekapitulasi data, laporan iuran bulanan, laporan status bayar kas, dan laporan detail keuangan, dengan opsi preview dan export e format PDF atau Excel.
6. Pengaturan & Personalisasi: Pengaturan preferensi aplikasi yang mencakup penentuan periode kas aktif, notifikasi penginagt, pilihan bahasa indonesia/inggris, serta peralihan mode gelap/dark mode.

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

**Informasi Api dan Teknologi**
1. Aplikasi ini dikembangkan menggunakan pendekatan Offline-First dan pola arsitektur MVVM dipadukan dengan Clean Arsitecture untuk memastikan keandalan performa.
2. Kotlin: Bahasa pemrograman utama.
3. Jetpack Compose + Material 3: Perangkat khusus untuk membangun antarmuka aplikasi UI modern yang lebih responsif dengan standar Material 3.
4. Hilt: Digunakan sebagai Dependency Injection untuk menyederhanakan struktur kode dan manajemen memori aplikasi.
5. Firebase Authentication: Layanan keamanan login menggunakan metode Email/Password Authentication.
6. Room Database & Firebase Firestore: Room digunakan untuk penyimpanan data lokal (konsep offline) dan Firestore untuk penyimpanan data online secara terpusat.
7. DataStore: Teknologi penyimpanan untuk mengatur preferensi pengguna.
8. Compose Navigation: Library untuk mengatur sistem navigasi antar halaman di dalam aplikasi.
9. StateFlow + ViewModel: Digunakan untuk manajemen state agar pembaruan data di antarmuka UI selalu sinkron.
10. Firebase Cloud Messaging/NotificationHelper _ WorkManager: Layanan untuk mengatur implementasi sistem pengiriman notifikasi pengingat ke pengguna.
11. PDF Generator (android-pdf-document) & Excel Generator (Poi-Android): Library pihak ketiga yang berfungsi mengeksekudi proses pembuatan dan pengeksporan file laporan keuangan menjadi dokumen PDF maupun Excel.
12. COil: Digunakan untuk memuat aset gambar antarmuka dengan lebih cepat dan ringan.
13. Nager.Date API: API publik eksternal yang datanya ditampilkan di halaman home untuk memberikan informasi lbur nasional.
14. Retrofit & KotlinX Serialization: Retrofit digunakan sebagai HTTP Client untuk memanggil API publik, dan KotlinX Serialization digunakan untuk melakukan parsing data JSON.

