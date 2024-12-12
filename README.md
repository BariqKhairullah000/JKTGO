# JKT GO! - Your Jakarta Travel Companion

## Deskripsi
JKT GO! adalah aplikasi chatbot berbasis Android yang dirancang untuk meningkatkan pengalaman wisata di Jakarta. Aplikasi ini menyediakan informasi perjalanan real-time dan akurat melalui antarmuka chatbot yang interaktif.

### Latar Belakang
- Jakarta, sebagai salah satu kota metropolitan terbesar di dunia, menghadapi tantangan dalam hal penyebaran informasi wisata yang tersebar dan tidak konsisten
- Wisatawan sering kesulitan menemukan informasi perjalanan yang lengkap dan relevan dalam satu platform
- Kebutuhan akan solusi teknologi untuk memudahkan perjalanan sangat penting di era digital ini

### Fitur Utama
- Chatbot interaktif untuk bantuan wisata
- Informasi real-time tentang destinasi wisata
- Rekomendasi lokasi berbasis lokasi
- Pembaruan event dan diskon wisata
- Fokus pada promosi destinasi lokal

## Persyaratan Sistem
- Android Studio Koala Feature Drop (2024.1.2)
- JDK (Java Development Kit) 1.8
- Minimum Android SDK: API 24 (Android 7.0 Nougat)
- Target SDK: API 34 (Android 14)
- RAM minimal 4GB (direkomendasikan 8GB)
- Ruang penyimpanan kosong minimal 2GB
- Koneksi Internet

## Spesifikasi Teknis
- Bahasa Pemrograman: Kotlin
- Architecture Pattern: MVVM
- Version Code: 1
- Version Name: 1.0

## Langkah-langkah Instalasi

### 1. Clone Repository
```bash
git clone https://github.com/BariqKhairullah000/JKTGO.git
cd JKTGO
```

### 2. Buka dan Setup Project
1. Buka Android Studio
2. Pilih "Open an Existing Project"
3. Arahkan ke direktori tempat Anda meng-clone repository
4. Tunggu proses build gradle selesai
5. Pastikan semua dependencies terdownload dengan baik

### 3. Struktur Proyek
```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.bangkitbariq.jktgo/
│   │   │       ├── chatbot/
│   │   │       │   ├── APIModels.kt
│   │   │       │   ├── ChatAdapter
│   │   │       │   ├── ChatbotActivity
│   │   │       │   ├── ChatbotAPI
│   │   │       │   ├── ChatbotViewModel.kt
│   │   │       │   └── ChatMessage
│   │   │       ├── data/
│   │   │       │   └── HotelResponse
│   │   │       └── utils/
│   │   │           ├── NetworkUtils
│   │   │           ├── MainActivity
│   │   │           └── ProfileActivity
│   │   └── res/
│   └── androidTest/
```

### 4. Dependencies
```gradle
dependencies {
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
    
    // JSON Parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
    
    // AndroidX Components
    implementation 'androidx.core:core-ktx:latest.version'
    implementation 'androidx.appcompat:appcompat:latest.version'
    implementation 'androidx.recyclerview:recyclerview:latest.version'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'androidx.activity:activity-ktx:1.7.2'
}
```

## Permissions
Aplikasi memerlukan beberapa permission:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Cara Penggunaan
1. Buka aplikasi JKT GO!
2. Mulai percakapan dengan chatbot
3. Tanyakan informasi tentang destinasi wisata, event, atau rekomendasi

## Troubleshooting

### Masalah Umum dan Solusinya

1. **Gradle Build Error**
   - Clean project (Build > Clean Project)
   - Invalidate Cache/Restart (File > Invalidate Caches > Invalidate and Restart)
   - Pastikan koneksi internet stabil

2. **Network Error**
   - Periksa koneksi internet
   - Pastikan permission internet sudah ditambahkan di AndroidManifest.xml

3. **Build Timeout**
   - Tingkatkan heap size di gradle.properties
   - Pastikan komputer memiliki RAM yang cukup
   - Tutup aplikasi yang tidak digunakan

## Tim Pengembang
### Mobile Development Team (Bangkit Academy 2024)
1. Bariq Khairullah
   - Institusi: Universitas Sriwijaya
   - Program: Bangkit Academy 2024
   - Email: ariqsyas30@gmail.com
   - LinkedIn: [Bariq Khairullah](http://www.linkedin.com/in/bariqkhairullah)
   - GitHub: [BariqKhairullah000](https://github.com/BariqKhairullah000)

2. Dika Prasetia
   - Institusi: Universitas Sriwijaya
   - Program: Bangkit Academy 2024
   - Email: dprasetya060@gmail.com
   - LinkedIn: [Dika Prasetia](http://www.linkedin.com/in/dikaprasetia)
   - GitHub: [DikaPrasetia](https://github.com/DikaPrasetia)

## Institusi
Proyek ini dikembangkan sebagai bagian dari program:
**Bangkit Academy 2024 By Google, GoTo, Tokopedia, Traveloka**

## Kontribusi
Jika Anda ingin berkontribusi pada proyek ini:
1. Fork repository
2. Buat branch baru (`git checkout -b fitur-baru`)
3. Commit perubahan (`git commit -m 'Menambah fitur baru'`)
4. Push ke branch (`git push origin fitur-baru`)
5. Buat Pull Request

## Bug Reports & Support
Untuk melaporkan bug atau mendapatkan bantuan:
- Buat issue baru di [GitHub Issues](https://github.com/BariqKhairullah000/JKTGO/issues)
- Kontak tim pengembang melalui email yang tercantum di atas
