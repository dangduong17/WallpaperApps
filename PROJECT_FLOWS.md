# TÀI LIỆU CHI TIẾT CÁC MÀN HÌNH & LUỒNG CHỨC NĂNG (PROJECT FLOWS) - PION-BASE

Tài liệu này tổng hợp toàn bộ các tính năng, kiến trúc, sơ đồ luồng hoạt động (Data Flow) và chi tiết các hàm xử lý của từng **Screen Function** trong dự án **Pion-Base** (Ứng dụng Quản lý & Cài đặt Hình nền Android).

---

## 🏛️ 1. TỔNG QUAN KIẾN TRÚC & MÔ HÌNH THIẾT KẾ

Dự án tuân thủ nghiêm ngặt **Kiến trúc 3 lớp (3-Layer Architecture)** kết hợp với mô hình **MVVM (Model-View-ViewModel)** và **Koin Dependency Injection**:

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 UI LAYER (Presentation)                                 │
│   Fragments / Activities <──> ViewModels (StateFlow / Channel / Custom UI Events)       │
└───────────────────────────────────────────┬─────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    DOMAIN LAYER                                         │
│       Single-Responsibility UseCases (Chứa Business Logic chính, ví dụ: SetWallpaper)   │
└───────────────────────────────────────────┬─────────────────────────────────────────────┘
                                            │
                                            ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                     DATA LAYER                                          │
│   Repositories <──> Local DAO (Room) / Remote DataSource / DataStore / MediaStore       │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### Các nguyên tắc nền tảng (Core Principles):
1. **Single-Activity Architecture**: Sử dụng duy nhất `MainActivity` với Jetpack Navigation Component (`nav_main.xml`).
2. **Base Classes Bắt buộc**:
   - `BaseFragment<Binding, ViewModel>`
   - `BaseViewModel<State, Event>`
   - `BaseListAdapter<Item, ViewBinding>`
   - `BaseDialogFragment<Binding>`, `BaseBottomSheetDialogFragment<Binding>`
3. **Mô hình 3-File theo Feature**: Mỗi màn hình chính chia rõ vai trò:
   - `{Feature}Fragment.kt`: UI chính, đăng ký Observers (`collectFlowOnView`).
   - `{Feature}FragmentEx.kt`: Extension functions chứa sự kiện click, khởi tạo View.
   - `{Feature}ViewModel.kt`: Quản lý StateFlow `uiState` và Channel `uiEvent`.
4. **Không tương tác trực tiếp Repository từ ViewModel**: ViewModel bắt buộc inject và thực thi qua **UseCase**.

---

## 📋 2. BẢNG TỔNG HỢP CÁC CHỨC NĂNG & MÀN HÌNH (FEATURE MATRIX)

| STT | Feature (Use Case Group) | Screen / Function Name | Thành phần tham gia chính |
|---|---|---|---|
| **1** | CORE ARCHITECTURE | Project Initialization | `MyApplication`, Base Classes, Koin DI Modules |
| **2** | USER EXPERIENCE & FLOW | Onboarding Screen | `OnboardFragment`, `OnboardViewModel`, `DataStore` |
| **3** | LIVE ENGINE SERVICE | Local Database System | `AppDatabase`, `CategoryDao`, `WallpaperDao` (Room DB) |
| **4** | MEDIA DISPLAY | Home Feed & Wallpaper List | `HomeFragment`, `HomeViewModel`, `CategoryAdapter`, `Glide` |
| **5** | MEDIA DISPLAY | System Photo Picker | `SettingFragment`, `EditWallpaperActivity`, Photo Picker API |
| **6** | MEDIA DISPLAY | Set Wallpaper via URL | `WallpaperPreviewBottomSheet`, `DownloadImageToBitmapUseCase` |
| **7** | DATA PERSISTENCE | Favorite Collection | `FavoriteFragment`, `FavoriteViewModel`, `WallpaperDao` |
| **8** | SEARCH & DISCOVERY | Search & Filter System | `SearchFragment`, `SearchViewModel`, `SuggestionAdapter` |
| **9** | LIVE ENGINE SERVICE | Live Wallpaper Service (GIF/Video) | `LiveWallpaperService`, `VideoWallpaperService`, `SurfaceHolder` |
| **10** | MEDIA STORAGE | Download & Scoped Storage | `DownloadWallpaperUseCase`, `MediaStore API` |
| **11** | USER EXPERIENCE & FLOW | Splash & Multi-Language System | `SplashFragment`, `LanguageFragment`, `LanguageManager` |
| **12** | USER EXPERIENCE & FLOW | Dark Mode & Material You | `ThemeManager`, `SettingFragment`, `AppCompatDelegate` |
| **13** | MEDIA INTERACTION | Text & Quote on Wallpaper Editor | `QuoteEditorFragment`, `StickerTextView`, `QuoteAdapter` |
| **14** | MEDIA INTERACTION | Quick Share & Social Integration | `FileProvider`, `Intent.ACTION_SEND`, ShareSheet |
| **15** | SETTINGS & UTILITY | Cache Management & Cleaner | `SettingFragment`, `CacheRepositoryImpl`, `Glide Cache` |
| **16** | SYSTEM & ACCESSIBILITY | Dynamic Battery Saver Mode | `BatterySaverManager`, `LiveWallpaperService`, BroadcastReceiver |
| **17** | AUTOMATIC ENGINE | Auto Change Wallpaper Engine | `AutoWallpaperWorker`, `WorkManager`, `DataStore` |

---

## 🔍 3. CHI TIẾT TỪNG SCREEN FUNCTION & LUỒNG XỬ LÝ

---

### 1️⃣ Project Initialization (Core Architecture)

* **Mục tiêu**: Khởi tạo cấu trúc nền tảng ứng dụng Android, thiết lập Clean Architecture 3 lớp, Koin Dependency Injection, Room Database và DataStore.
* **Các hàm tham gia**:
  - `MyApplication.onCreate()`: Điểm khởi tạo toàn cục ứng dụng.
  - `MyApplication.startKoin()`: Nạp danh sách tất cả DI modules (`appModules`).
  - `ThemeManager.init(application, dataStoreRepository)`: Lắng nghe và thiết lập chế độ giao diện (Dark/Light/Dynamic Color).
  - `LanguageManager.setLocale(context, languageCode)`: Cấu hình ngôn ngữ hệ thống.
  - các Module DI: `CoreModule`, `NetworkModule`, `DatabaseModule`, `RepositoryModule`, `UseCaseModule`, `ViewModelModule`, `WorkerModule`.
* **Sơ đồ luồng**:
```
[Application Startup] ──► [MyApplication.onCreate()]
                                  │
                                  ├─► Init Timber & Koin (startKoin)
                                  ├─► Set WorkManager Factory (KoinWorkerFactory)
                                  ├─► ThemeManager.init() (Đọc DataStore & set Night Mode)
                                  └─► LanguageManager.setLocale() (Cấu hình Locale)
```

---

### 2️⃣ Onboarding Screen (`OnboardFragment`)

* **Mục tiêu**: Phát triển màn giới thiệu tính năng (Carousel 5 màn hình) và đồng bộ trạng thái lần đầu mở ứng dụng qua DataStore.
* **Các hàm tham gia**:
  - `OnboardFragment.initView()`: Khởi tạo `ViewPager2` kết hợp `OnboardFragmentStateAdapter` quản lý 5 Sub-Fragments (`OnboardScreen1Fragment` ... `OnboardScreen5Fragment`).
  - `OnboardFragmentEx.settingEvent()`: Lắng nghe sự kiện người dùng bấm Next, Skip hoặc Start.
  - `OnboardViewModel.completeOnboarding()`: Gọi `SetIsFirstLaunchUseCase(false)`.
  - `SetIsFirstLaunchUseCase.invoke()` -> `DataStoreRepositoryImpl.setIsFirstLaunch()`.
  - `navigator.navigateTo(R.id.action_onboardFragment_to_homeFragment)`: Điều hướng đến Màn hình chính.
* **Sơ đồ luồng**:
```
[OnboardFragment]
       │
       ├─► Người dùng lướt qua 5 Slide (ViewPager2)
       ├─► Bấm "Next" / "Skip" / "Get Started"
       │
       ▼
[OnboardViewModel.completeOnboarding()]
       │
       ▼
[SetIsFirstLaunchUseCase(false)] ──► [DataStore: isFirstLaunchKey = false]
       │
       ▼
[Navigator.navigateTo(HomeFragment)]
```

---

### 3️⃣ Local Database System (Room Database)

* **Mục tiêu**: Thiết kế cơ sở dữ liệu SQLite/Room lưu trữ danh mục ảnh, danh sách yêu thích và bộ nhớ tạm offline.
* **Các hàm tham gia**:
  - `AppDatabase`: Singleton Room Database quản lý `CategoryEntity`, `WallpaperEntity`, `DummyEntity`.
  - `CategoryDao`: `getCategories()`, `insertCategories()`.
  - `WallpaperDao`: `getFavoriteWallpapers()`, `getFeaturedWallpapers()`, `getTopWallpapers()`, `getWallpapersByCategory()`, `searchWallpapers()`, `toggleFavorite()`, `isFavorite()`.
  - `WallpaperRepositoryImpl`: Điều phối dữ liệu từ Room DB và Remote DataSource sang dạng `Flow<Result<T>>`.
* **Sơ đồ luồng**:
```
[UseCases] <──► [WallpaperRepositoryImpl] <──► [WallpaperDao / CategoryDao]
                                                        │
                                                        ▼
                                              [SQLite Local Room DB]
                                           (CategoryEntity, WallpaperEntity)
```

---

### 4️⃣ Home Feed & Wallpaper List (`HomeFragment`)

* **Mục tiêu**: Thiết kế giao diện Home hiển thị danh mục hình nền, xu hướng nổi bật (Featured), hình nền hàng đầu (Top) và tối ưu hóa tải ảnh tĩnh bằng Glide.
* **Các hàm tham gia**:
  - `HomeFragment.initView()`: Cấu hình các RecyclerView adapter: `CategoryAdapter`, `FeaturedAdapter`, `TopWallpaperAdapter`, `FilterAdapter`.
  - `HomeViewModel.loadData()`: Thực thi đồng thời các UseCases bằng `handleApiCall()`:
    - `GetCategoriesUseCase()`
    - `GetFeaturedWallpapersUseCase()`
    - `GetTopWallpapersUseCase()`
  - `HomeFragment.subscribeObserver()`: Quan sát `uiState` (`categories`, `featuredWallpapers`, `topWallpapers`) bằng `collectFlowOnView()`.
  - `WallpaperPreviewBottomSheet`: Hiển thị xem trước hình nền chi tiết khi click vào một item.
* **Sơ đồ luồng**:
```
[HomeFragment.onCreateView()] ──► [HomeViewModel.loadData()]
                                            │
               ┌────────────────────────────┼────────────────────────────┐
               ▼                            ▼                            ▼
   [GetCategoriesUseCase]     [GetFeaturedWallpapersUseCase]   [GetTopWallpapersUseCase]
               │                            │                            │
               └────────────────────────────┼────────────────────────────┘
                                            ▼
                           Emits StateFlow: HomeUiState
                                            │
                                            ▼
                           [HomeFragment.subscribeObserver()]
                                            │
                                            ▼
                     Cập nhật Adapters & Load ảnh mượt bằng Glide
```

---

### 5️⃣ System Photo Picker Integration (`PhotoPicker`)

* **Mục tiêu**: Tích hợp Photo Picker API chuẩn Android cho phép truy cập ảnh/video cá nhân của người dùng một cách an toàn mà không cần xin quyền lưu trữ (Storage Permission).
* **Các hàm tham gia**:
  - `ActivityResultContracts.PickVisualMedia()`: Đăng ký Launcher chuẩn của Android.
  - `SettingFragmentEx.btnPickPhoto`: Kích hoạt `pickMediaLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))`.
  - `EditWallpaperActivity.onCreate()`: Nhận `Uri` được chọn từ Intent.
  - `UCrop.of(sourceUri, destinationUri)`: Mở thư viện cắt ảnh chuẩn tỷ lệ 9:16 đối với ảnh tĩnh.
* **Sơ đồ luồng**:
```
[SettingFragment] ──► Bấm chọn ảnh từ thiết bị
        │
        ▼
[Photo Picker API] (`PickVisualMedia`) ──► Người dùng chọn 1 File Ảnh/Video
        │
        ▼
[EditWallpaperActivity] (Nhận Uri)
        │
        ├─► Ảnh Video/GIF ───────────────────────► Chuyển thẳng sang Live Wallpaper Engine
        └─► Ảnh Tĩnh (JPEG/PNG) ──► [UCrop] ────► Cắt ảnh 9:16 ──► [Apply Static Wallpaper]
```

---

### 6️⃣ Set Wallpaper via URL (`Url Wallpaper`)

* **Mục tiêu**: Xây dựng giao diện xem trước (Preview) và cài đặt hình nền trực tiếp từ đường dẫn URL bất kỳ trên Internet thông qua Glide & WallpaperManager.
* **Các hàm tham gia**:
  - `WallpaperPreviewBottomSheet`: Hiển thị ảnh xem trước từ URL.
  - `DownloadImageToBitmapUseCase.invoke(url)`: Sử dụng Glide để tải InputStream từ URL và decode thành `Bitmap`.
  - `SetWallpaperUseCase.invoke(bitmap, flag)`: Đặt hình nền qua `WallpaperManager.getInstance(context).setBitmap(bitmap, null, true, flag)`.
  - Tham số `flag`: `WallpaperManager.FLAG_SYSTEM` (Màn hình chính), `WallpaperManager.FLAG_LOCK` (Màn hình khóa) hoặc cả hai.
* **Sơ đồ luồng**:
```
[WallpaperPreviewBottomSheet] ──► Bấm "Set Wallpaper"
                                           │
                                           ▼
                       [DownloadImageToBitmapUseCase(url)]
                                           │
                                           ▼
                             [Glide.asBitmap().submit()]
                                           │
                                           ▼
                             [SetWallpaperUseCase(bitmap, flag)]
                                           │
                                           ▼
                      [WallpaperManager.getInstance().setBitmap()]
                                           │
                                           ▼
                           Hiển thị Toast Cài đặt thành công
```

---

### 7️⃣ Favorite Collection (`FavoriteFragment`)

* **Mục tiêu**: Màn hình lưu trữ bộ sưu tập hình nền yêu thích & quản lý dữ liệu lưu trữ offline bằng Room DB.
* **Các hàm tham gia**:
  - `FavoriteFragment.subscribeObserver()`: Lắng nghe danh sách ảnh yêu thích từ `FavoriteViewModel.uiState.favoriteWallpapers`.
  - `FavoriteViewModel.loadFavorites()`: Gọi `GetFavoriteWallpapersUseCase()`.
  - `ToggleFavoriteUseCase.invoke(wallpaperId)`: Đảo ngược trạng thái `isFavorite` của `WallpaperEntity` trong Room DB.
  - `WallpaperDao.getFavoriteWallpapers()`: Truy vấn danh sách ảnh có `isFavorite = 1`.
* **Sơ đồ luồng**:
```
[User Click Heart Icon] ──► [ToggleFavoriteUseCase(id)] ──► Cập nhật Room DB
                                                                  │
                                                                  ▼
[FavoriteFragment] ◄── [GetFavoriteWallpapersUseCase()] ◄── [WallpaperDao]
        │
        ▼
Cập nhật RecyclerView & Dùng danh sách này cho Auto Wallpaper Engine
```

---

### 8️⃣ Search & Filter System (`SearchFragment`)

* **Mục tiêu**: Tìm kiếm hình nền theo từ khóa, lọc theo màu sắc và chủ đề với giao diện gợi ý linh hoạt.
* **Các hàm tham gia**:
  - `SearchFragmentEx.settingEvent()`: Đăng ký sự kiện thay đổi văn bản trên `edtSearch`.
  - `SearchViewModel.search(query)`: Kích hoạt `SearchWallpapersUseCase(query)`.
  - `WallpaperDao.searchWallpapers("%query%")`: Truy vấn SQL `LIKE %query%` theo tên hoặc danh mục.
  - `SuggestionAdapter`: Khai báo và hiển thị khung từ khóa gợi ý khớp hoàn toàn bên dưới thanh tìm kiếm.
  - `FilterAdapter`: Cho phép lọc nhanh theo màu sắc chủ đạo (Red, Blue, Green, Black,...) hoặc chủ đề.
* **Sơ đồ luồng**:
```
[SearchFragment] ──► Người dùng nhập từ khóa "Nature"
        │
        ▼
[SearchViewModel.search()] ──► [SearchWallpapersUseCase]
                                        │
                                        ▼
                            [WallpaperDao.searchWallpapers()]
                                        │
                                        ▼
                Trả về kết quả ──► Cập nhật RecyclerView & SuggestionAdapter
```

---

### 9️⃣ Live Wallpaper Service (GIF / Video Live Engine)

* **Mục tiêu**: Đặt video / GIF / ảnh động làm hình nền hệ thống bằng `WallpaperService` và `SurfaceHolder`.
* **Các hàm tham gia**:
  - `EditWallpaperActivity.setupLiveWallpaper()`:
    - Ghi file GIF/Video vào bộ nhớ riêng ứng dụng (`filesDir/active_gif.gif` hoặc `active_video.mp4`).
    - Ghi đường dẫn file vào SharedPreferences (`KEY_SELECTED_GIF_PATH`, `KEY_GIF_UPDATED_AT`).
    - Mở Intent cài đặt hình nền động: `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`.
  - `LiveWallpaperService.GifWallpaperEngine`:
    - `HandlerThread("GifWallpaperThread")`: Khởi chạy luồng phụ độc lập để vẽ khung hình.
    - `Movie.decodeStream()` / `Movie.decodeFile()`: 解码 (Decode) file GIF.
    - `drawFrame()`: Vẽ hình lên `SurfaceHolder` ở tần số ~30 FPS (`postDelayed(33ms)`).
    - `SharedPreferences.OnSharedPreferenceChangeListener`: Tự động nạp lại GIF khi người dùng đổi file mới.
  - `VideoWallpaperService.VideoWallpaperEngine`: Quản lý `MediaPlayer` gắn trên `SurfaceView` để chạy Video mượt mà.
* **Sơ đồ luồng**:
```
[EditWallpaperActivity] ──► Lưu file GIF vào filesDir/active_gif.gif
                                      │
                                      ▼
                        Cập nhật SharedPreferences
                                      │
                                      ▼
              Launch Intent: ACTION_CHANGE_LIVE_WALLPAPER
                                      │
                                      ▼
             [LiveWallpaperService] -> [GifWallpaperEngine]
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
   [HandlerThread phụ]                           [Movie.decodeStream()]
              │                                               │
              └───────────────────────┬───────────────────────┘
                                      ▼
                        [drawFrame() trên SurfaceHolder]
```

---

### 🔟 Download & Scoped Storage (`DownloadWallpaperUseCase`)

* **Mục tiêu**: Tải ảnh/video chất lượng cao về thư viện máy và xử lý chuẩn Scoped Storage trên Android 10+.
* **Các hàm tham gia**:
  - `DownloadWallpaperUseCase.invoke(url)` -> `WallpaperRepositoryImpl.downloadWallpaper(url)`.
  - `OkHttpClient`: Tải Stream dữ liệu từ URL.
  - `MediaStore.Images.Media.EXTERNAL_CONTENT_URI`: Chèn metadata file vào thư mục `Pictures/PionBase`.
  - `ContentResolver.insert()` & `outputStream.use`: Ghi file an toàn không bị leak memory.
* **Sơ đồ luồng**:
```
[User Click Download] ──► [DownloadWallpaperUseCase]
                                   │
                                   ▼
                   [WallpaperRepositoryImpl.downloadWallpaper()]
                                   │
                                   ▼
                   [OkHttpClient Download Stream]
                                   │
                                   ▼
             [MediaStore API: RelativePath = Pictures/PionBase]
                                   │
                                   ▼
                  Ghi vào Storage & Báo Toast thành công
```

---

### 11. Splash & Multi-Language System (`SplashFragment` & `LanguageFragment`)

* **Mục tiêu**: Xây dựng màn hình khởi động (Splash) và tính năng chuyển đổi ngôn ngữ ứng dụng tức thì không cần khởi động lại máy.
* **Các hàm tham gia**:
  - `SplashFragment`: Đợi 2 giây -> Kiểm tra `GetIsFirstLaunchUseCase()` -> Điều hướng đến `OnboardFragment` (lần đầu) hoặc `HomeFragment`.
  - `LanguageFragment` / `ChangeLanguageFragment`: Danh sách ngôn ngữ nạp qua `GetLanguagesUseCase()`.
  - `SetLanguageUseCase.invoke(languageCode)` -> `DataStoreRepositoryImpl.setLanguage()`.
  - `LanguageManager.setLocale(context, languageCode)`: Cập nhật `Configuration.setLocale()` cho ứng dụng.
  - `Activity.recreate()` / restart `MainActivity`: Áp dụng ngay giao diện đa ngôn ngữ mới.
* **Sơ đồ luồng**:
```
[LanguageFragment] ──► Chọn ngôn ngữ (Ví dụ: Tiếng Việt "vi")
        │
        ▼
[SetLanguageUseCase("vi")] ──► Lưu vào DataStore
        │
        ▼
[LanguageManager.setLocale()] ──► Cập nhật Context & Configuration
        │
        ▼
[Activity.recreate()] ──► Giao diện ứng dụng đổi ngôn ngữ lập tức
```

---

### 12. Dark Mode & Dynamic Theming (`ThemeManager`)

* **Mục tiêu**: Hỗ trợ Light/Dark Theme tự động và tích hợp Dynamic Color (Material You) theo tông màu hệ thống.
* **Các hàm tham gia**:
  - `ThemeManager.init(application, dataStoreRepository)`: Lắng nghe cài đặt từ DataStore ngay khi khởi chạy app.
  - `ThemeManager.applyThemeMode(themeMode)`: Sử dụng `AppCompatDelegate.setDefaultNightMode()` (`MODE_NIGHT_NO`, `MODE_NIGHT_YES`, `MODE_NIGHT_FOLLOW_SYSTEM`).
  - `ThemeManager.setDynamicColorEnabled(enabled)`: Tích hợp `DynamicColors.applyToActivitiesIfAvailable()`.
  - `SettingFragmentEx.showThemeDialog()`: Hiển thị Dialog chọn chế độ Sáng / Tối / Hệ thống.
* **Sơ đồ luồng**:
```
[SettingFragment] ──► Chọn chế độ Giao diện (Sáng / Tối / Hệ thống)
        │
        ▼
[SetThemeSettingsUseCase] ──► [DataStore: themeModeKey]
        │
        ▼
[ThemeManager.applyThemeMode()] ──► [AppCompatDelegate.setDefaultNightMode()]
```

---

### 13. Text & Quote on Wallpaper Editor (`QuoteEditorFragment`)

* **Mục tiêu**: Cho phép thêm văn bản/câu châm ngôn lên ảnh: tùy biến font chữ nghệ thuật, cỡ chữ, căn lề, màu sắc, bóng đổ và vị trí trước khi lưu hoặc cài đặt.
* **Các hàm tham gia**:
  - `QuoteEditorFragment`: Nhận dữ liệu hình nền từ Navigation Args (`args.wallpaper`).
  - `StickerTextView`: Custom View hỗ trợ hiển thị văn bản với tính năng:
    - Font nghệ thuật: `FontAdapter` nạp các font trong `res/font/`.
    - Bảng màu: `ColorAdapter` cho phép chọn màu sắc.
    - Căn lề: `ALIGN_NORMAL` (Trái), `ALIGN_CENTER` (Giữa), `ALIGN_OPPOSITE` (Phải).
    - Cỡ chữ: Điều chỉnh bằng `Slider` (12sp - 60sp).
  - `QuoteBottomSheet`: Cho phép chọn các câu trích dẫn có sẵn phân theo chủ đề (Love, Life, Motivation,...) từ `GetQuotesUseCase()`.
  - `StickerTextView.renderToBitmap(bgBitmap)`: Tổng hợp hình nền gốc và văn bản custom thành 1 file Bitmap nguyên bản.
  - `SaveQuoteWallpaperUseCase.invoke(bitmap)`: Lưu kết quả vào thư viện ảnh máy.
* **Sơ đồ luồng**:
```
[QuoteEditorFragment] ──► Hiển thị Ảnh nền gốc
        │
        ├─► Mở QuoteBottomSheet ──► Chọn câu Châm ngôn hay
        ├─► Chọn Font nghệ thuật / Chọn màu / Căn lề / Chỉnh cỡ chữ
        │
        ▼
[StickerTextView.renderToBitmap(bgBitmap)] (Gộp chữ & ảnh)
        │
        ├─► [Save Button] ────► [SaveQuoteWallpaperUseCase] ──► Lưu vào Máy
        └─► [Apply Button] ───► [SetWallpaperUseCase] ────────► Đặt làm Hình nền
```

---

### 14. Quick Share & Social Integration (`Quick Share`)

* **Mục tiêu**: Chia sẻ nhanh hình nền kèm liên kết lên các mạng xã hội (Instagram, Facebook, Zalo, Telegram) qua Android ShareSheet chuẩn (`Intent.ACTION_SEND`).
* **Các hàm tham gia**:
  - `FileProvider.getUriForFile()`: Tạo `content://` URI an toàn cấp quyền truy cập tạm thời cho ứng dụng bên ngoài.
  - `Intent(Intent.ACTION_SEND)`: Cấu hình MIME type `image/*`, gắn đính kèm `Intent.EXTRA_STREAM` và văn bản `Intent.EXTRA_TEXT`.
  - `Intent.createChooser()`: Khởi chạy Android Native ShareSheet.
* **Sơ đồ luồng**:
```
[User Click Share Button]
           │
           ▼
[Tạo File tạm từ Hình nền]
           │
           ▼
[FileProvider.getUriForFile()]
           │
           ▼
[Intent(Intent.ACTION_SEND)] + [Intent.createChooser()]
           │
           ▼
Hiển thị ShareSheet hệ thống (Chia sẻ sang Zalo, Facebook, Instagram,...)
```

---

### 15. Cache Management & Storage Cleaner (`Cache Management`)

* **Mục tiêu**: Màn hình Cài đặt cho phép xem dung lượng bộ nhớ đệm (Cache) của ảnh/video và tùy chọn xóa sạch bộ nhớ đệm (Glide cache) để giải phóng bộ nhớ thiết bị.
* **Các hàm tham gia**:
  - `GetCacheSizeUseCase.invoke()` -> `CacheRepositoryImpl.getCacheSize()`: Tính tổng dung lượng các file trong `context.cacheDir` và `context.externalCacheDir`.
  - `FormatCacheSizeUseCase.invoke(bytes)`: Chuyển số bytes thành chuỗi định dạng dễ đọc (B, KB, MB, GB).
  - `ClearCacheUseCase.invoke()` -> `CacheRepositoryImpl.clearCache()`:
    - `Glide.get(context).clearDiskCache()`: Xóa cache ổ đĩa của Glide (chạy trên IO thread).
    - `Glide.get(context).clearMemory()`: Xóa cache RAM của Glide (chạy trên Main thread).
    - `deleteDirContents()`: Xóa toàn bộ file rác trong thư mục cache.
* **Sơ đồ luồng**:
```
[SettingFragment] ──► Lấy dung lượng qua GetCacheSizeUseCase
        │
        ▼
Hiển thị dung lượng (Ví dụ: "45.8 MB")
        │
        ▼ Bấm "Clear Cache"
        │
[ClearCacheUseCase] ──► [Glide.clearDiskCache() + Glide.clearMemory()]
        │
        ▼
Xóa toàn bộ file cache ──► Cập nhật lại UI hiển thị "0 B"
```

---

### 16. Dynamic Battery Saver Mode (`BatterySaverManager`)

* **Mục tiêu**: Tự động hạ độ phân giải hoặc tạm dừng Live Wallpaper động khi pin yếu (< 20%) hoặc khi người dùng bật chế độ Tiết kiệm pin hệ thống.
* **Các hàm tham gia**:
  - `BatterySaverManager.isLowBatteryOrPowerSave(context)`: Kiểm tra nếu dung lượng pin < 20% hoặc `PowerManager.isPowerSaveMode == true`.
  - `BatterySaverManager.observeBatterySaverState(context)`: Sử dụng `callbackFlow` lắng nghe các Broadcast Receiver:
    - `Intent.ACTION_BATTERY_CHANGED`
    - `PowerManager.ACTION_POWER_SAVE_MODE_CHANGED`
  - `SettingFragmentEx.batterySaverEvent()`: Cho phép người dùng bật/tắt tính năng Tiết kiệm pin này trong Cài đặt.
  - `LiveWallpaperService.observeBatterySaver()`: Tự động hủy luồng vẽ khung hình (`renderHandler?.removeCallbacks(frameRunnable)`) khi trạng thái tiết kiệm pin kích hoạt, giúp giảm 0% mức tiêu thụ pin/CPU.
* **Sơ đồ luồng**:
```
[System Broadcast: Pin yếu < 20% hoặc Bật Tiết kiệm pin]
                           │
                           ▼
          [BatterySaverManager.observeBatterySaverState()]
                           │
                           ▼
              [LiveWallpaperService.collect()]
                           │
                           ▼
          [isBatterySaverActive = true]
                           │
                           ▼
 Tạm dừng renderHandler loop ──► Tiết kiệm tối đa Pin & CPU
```

---

### 17. Automatic Wallpaper Engine (`AutoWallpaperWorker`)

* **Mục tiêu**: Tự động đổi hình nền hệ thống theo chu kỳ đã cài đặt (từ 15 phút đến 24 giờ) lấy từ danh sách **Ảnh Yêu thích (Favorites)** của người dùng.
* **Các hàm tham gia**:
  - `SettingFragmentEx.showAutoWallpaperIntervalDialog()`: Hiển thị Dialog chọn khoảng thời gian (NumberPicker 15 phút - 1440 phút).
  - `WorkManager.enqueueUniquePeriodicWork()`: Đăng ký công việc chạy ngầm định kỳ với tên duy nhất `AutoWallpaperWork`.
  - `AutoWallpaperWorker.doWork()`:
    1. Đọc danh sách ảnh Yêu thích từ `wallpaperRepository.getFavoriteWallpapers()`.
    2. Lọc ngẫu nhiên 1 ảnh khác với ảnh lượt trước (`lastUsedUrl`).
    3. Thêm timestamp vào URL ảnh (`url?t={timestamp}`) để tránh Glide cache lại ảnh cũ.
    4. Tải Bitmap từ Glide trên `Dispatchers.IO`.
    5. Đặt làm hình nền qua `WallpaperManager.getInstance(applicationContext).setBitmap(bitmap)`.
* **Sơ đồ luồng**:
```
[SettingFragment] ──► Bật "Auto Change Wallpaper" & Chọn chu kỳ (Ví dụ: 60 phút)
        │
        ▼
[WorkManager.enqueueUniquePeriodicWork("AutoWallpaperWork")]
        │
        ▼ (Hệ thống kích hoạt ngầm mỗi 60 phút)
[AutoWallpaperWorker.doWork()]
        │
        ├─► [wallpaperRepository.getFavoriteWallpapers()] (Lấy từ Room DB)
        ├─► Chọn ngẫu nhiên 1 bức ảnh chưa dùng
        ├─► Download Bitmap qua Glide (Dispatchers.IO)
        └─► [WallpaperManager.setBitmap(bitmap)] ──► Đổi thành công hình nền
```

---

## 🛠️ TỔNG HỢP QUY CHUẨN CODE (CODING CONVENTIONS)

| Thành phần | Quy chuẩn áp dụng |
|---|---|
| **Base Usage** | Fragment kế thừa `BaseFragment`, ViewModel kế thừa `BaseViewModel`, Adapter kế thừa `BaseListAdapter`. |
| **Coroutines** | Dùng `launchIO { }`, `launchMain { }` (đã tích hợp Handler Exception). **KHÔNG** dùng `lifecycleScope.launch` trực tiếp. |
| **State Collection** | Sử dụng `collectFlowOnView` đi kèm `map` và `distinctUntilChanged` để tránh re-render thừa. |
| **Click Listener** | Bắt buộc dùng `setPreventDoubleClick { }` hoặc `setPreventDoubleClickScaleView { }`. |
| **Show Dialog** | Bắt buộc dùng `safeShowDialog()` hoặc `safeShowBottomSheet()`. |
| **Navigation** | Dùng `navigator.navigateTo()` và `navigator.navigateUp()`. |

---
*Tài liệu được cập nhật đầy đủ và chuẩn xác nhất theo toàn bộ mã nguồn của dự án Pion-Base.*
