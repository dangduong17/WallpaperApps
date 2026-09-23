# TÀI LIỆU CHI TIẾT CÁC LUỒNG HOẠT ĐỘNG (PROJECT FLOWS) - PION-BASE

Tài liệu này giải thích chi tiết kiến trúc, sơ đồ dòng chảy dữ liệu (Data Flow) và các luồng chức năng chính trong ứng dụng **Pion-Base** (Ứng dụng Quản lý & Cài đặt Hình nền Android).

---

## 🏛️ 1. TỔNG QUAN KIẾN TRÚC DỰ ÁN

Dự án tuân thủ nghiêm ngặt **Kiến trúc 3 lớp (3-Layer Architecture)** kết hợp với mô hình **MVVM (Model-View-ViewModel)**:

```
┌────────────────────────────────────────────────────────────────────────┐
│                          UI LAYER (Presentation)                       │
│   Fragments / Activities <──> ViewModels (StateFlow / Channel / Events)  │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                             DOMAIN LAYER                               │
│      Single-Responsibility UseCases (Chứa Business Logic chính)        │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                              DATA LAYER                                │
│   Repositories <──> Local DAO (Room) / Remote DataSource / DataStore   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 📋 2. CHI TIẾT CÁC LUỒNG CHỨC NĂNG CHÍNH

---

### 🖼️ LUỒNG 1: XEM VÀ TẢI ẢNH VỀ MÁY (View & Download Wallpaper)

Luồng này cho phép người dùng xem hình nền ở độ phân giải đầy đủ và tải hình nền về thư viện ảnh của thiết bị.

```
[WallpaperDetailFragment]
       │
       ├─► Người dùng nhấn nút Tải về (fabDownload)
       │
       ▼
[Check Permission] ──(Android < 10)──► Yêu cầu WRITE_EXTERNAL_STORAGE
       │
       └─(Android >= 10 / Granted)──► [WallpaperDetailViewModel.downloadWallpaper()]
                                              │
                                              ▼
                                 [DownloadWallpaperUseCase(url)]
                                              │
                                              ▼
                                [WallpaperRepositoryImpl.downloadWallpaper()]
                                              │
       ┌──────────────────────────────────────┴──────────────────────────────────────┐
       │ 1. OkHttpClient tải Stream dữ liệu từ URL                                   │
       │ 2. BitmapFactory.decodeStream() chuyển đổi sang Bitmap                       │
       │ 3. Tạo MediaStore ContentValues:                                            │
       │    - DISPLAY_NAME: PionBase_{timestamp}.jpg                                 │
       │    - RELATIVE_PATH: Pictures/PionBase                                       │
       │ 4. Chèn vào MediaStore.Images.Media.EXTERNAL_CONTENT_URI                     │
       └──────────────────────────────────────┬──────────────────────────────────────┘
                                              │
                                              ▼
                                 Emits Flow<Result<Uri>>
                                              │
                                              ▼
                         [WallpaperDetailFragment] (Nhận UI Event)
                                              │
                                              ▼
                             Hiển thị Dialog/Toast Thành công
```

#### Các thành phần tham gia:
* **UI**: `WallpaperDetailFragment`, `WallpaperDetailFragmentEx`, `WallpaperDetailViewModel`.
* **Domain**: `DownloadWallpaperUseCase`.
* **Data**: `WallpaperRepositoryImpl`, `OkHttpClient`, `MediaStore API`.

---

### 🎨 LUỒNG 2: XEM VÀ CÀI ĐẶT HÌNH NỀN (View & Set Wallpaper)

Luồng xử lý cài đặt hình nền từ màn hình chi tiết (`WallpaperDetailFragment`), phân biệt giữa **Hình nền Tĩnh (Static Image)** và **Hình nền Động (GIF / Video)**.

```
[WallpaperDetailFragment] ──► Người dùng nhấn "Set Wallpaper"
                                          │
                     ┌────────────────────┴────────────────────┐
                     │                                         │
               [Ảnh Tĩnh (Static)]                 [Ảnh Động (GIF / Video)]
                     │                                         │
                     ▼                                         ▼
   Hiển thị Dialog chọn Màn hình              Mở EditWallpaperActivity
   (Màn chính / Màn khóa / Cả hai)                           │
                     │                                         ▼
                     ▼                       Sao chép file vào internal storage:
   `WallpaperDetailViewModel.applyWallpaper()`   `filesDir/active_gif.gif` (hoặc video)
                     │                                         │
                     ▼                                         ▼
           [SetWallpaperUseCase]              Lưu đường dẫn & timestamp vào Prefs:
                     │                        `selected_gif_path`, `gif_updated_at`
                     ▼                                         │
  `WallpaperManager.setStream()` hoặc                         ▼
   `WallpaperManager.setBitmap()`            Khởi chạy Intent cài đặt Live Wallpaper:
                     │                        `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`
                     ▼                        Trỏ tới `LiveWallpaperService` / `VideoWallpaperService`
     Hiển thị Snackbar thành công                              │
                                                               ▼
                                              [LiveWallpaperService / VideoWallpaperService]
                                              - Chạy luồng vẽ phụ HandlerThread / SurfaceView
                                              - Tải GIF qua Movie hoặc Video qua MediaPlayer
                                              - Lắng nghe Prefs & File timestamp
```

#### Chi tiết kỹ thuật:
1. **Hình nền Tĩnh**: Sử dụng `WallpaperManager` gốc của Android truyền `FLAG_SYSTEM`, `FLAG_LOCK`, hoặc kết hợp cả hai.
2. **Hình nền GIF (Live Wallpaper)**:
   * Chuyển hướng qua `EditWallpaperActivity`.
   * Ghi nội dung file GIF vào bộ nhớ riêng của ứng dụng (`filesDir/active_gif.gif`).
   * Cập nhật SharedPreferences lưu `selected_gif_path` và `gif_updated_at`.
   * Gọi `LiveWallpaperService` sử dụng `android.graphics.Movie` chạy trên luồng phụ `HandlerThread("GifWallpaperThread")`.
3. **Hình nền Video (Live Video Wallpaper)**:
   * Sử dụng `VideoWallpaperService` quản lý `MediaPlayer` gắn mặt phẳng hiển thị `SurfaceView`, tự động `start()` / `pause()` theo vòng đời hiển thị màn hình để tiết kiệm pin tối đa.

---

### 🔄 LUỒNG 3: TỰ ĐỘNG ĐỔI HÌNH NỀN THEO CHU KỲ (Auto Change Wallpaper)

Cho phép hệ thống tự động thay đổi hình nền hệ thống từ danh sách **Ảnh Yêu Thích (Favorites)** của người dùng theo khoảng thời gian đã đặt.

```
┌────────────────────────────────────────────────────────────────────────┐
│                         BƯỚC 1: KÍCH HOẠT (SettingFragment)             │
└────────────────────────────────────────────────┬───────────────────────┘
                                                 │
   Người dùng Bật công tắc "swAutoChange" ───────┤
                                                 │
                                                 ▼
                             Hiển thị Dialog chọn thời gian (NumberPicker)
                             (Tối thiểu: 15 phút, Tối đa: 24 giờ)
                                                 │
                                                 ▼
                       Lưu cài đặt vào DataStore (Enable = true, Interval = X)
                                                 │
                                                 ▼
       [WorkManager.enqueueUniquePeriodicWork(WORK_NAME, UPDATE, PeriodicWorkRequest)]
```

```
┌────────────────────────────────────────────────────────────────────────┐
│                     BƯỚC 2: THỰC THI NGẦM (AutoWallpaperWorker)        │
└────────────────────────────────────────────────┬───────────────────────┘
                                                 │
            WorkManager kích hoạt ngầm theo chu kỳ (Dispatchers.IO)
                                                 │
                                                 ▼
                   [WallpaperRepository.getFavoriteWallpapers()]
                   Lấy danh sách ảnh Yêu thích từ Room Database
                                                 │
                                                 ▼
                      Kiểm tra danh sách:
                      - Nếu rỗng -> Worker báo Failure
                      - Nếu > 1 ảnh -> Chọn ngẫu nhiên ảnh khác ảnh vừa đổi
                                                 │
                                                 ▼
                    Tải Bitmap qua Glide bằng URL kèm Timestamp
                    (URL?t={timestamp} để tránh cache cũ)
                                                 │
                                                 ▼
                  [WallpaperManager.getInstance().setBitmap(bitmap)]
                  Đổi hình nền hệ thống thành công
```

#### Các thành phần tham gia:
* **UI/Settings**: `SettingFragment`, `SettingFragmentEx`, `SettingViewModel`.
* **Storage**: `DataStoreRepositoryImpl` (lưu trạng thái & chu kỳ).
* **Worker**: `AutoWallpaperWorker` (kế thừa `CoroutineWorker`).
* **Database**: `WallpaperDao.getFavoriteWallpapers()` (nguồn ảnh tự động).

---

### 📱 LUỒNG 4: CHỌN ẢNH VÀ GIF TỪ THƯ VIỆN NGƯỜI DÙNG (Gallery Photo & GIF Picker)

Luồng cho phép người dùng chọn bất kỳ ảnh hoặc GIF nào từ máy cá nhân để cắt ghép và đặt làm hình nền.

```
[SettingFragment] ──► Nhấn chọn ảnh từ máy (btnPickPhoto)
        │
        ▼
[Photo Picker API] (`ActivityResultContracts.PickVisualMedia`)
        │
        ▼ Người dùng chọn 1 File từ Gallery
        │
[EditWallpaperActivity] (Nhận Uri)
        │
        ├─────────────────────────────────────────┐
        │                                         │
  [Kiểm tra: Is GIF?]                       [Ảnh Tĩnh (JPEG/PNG)]
        │                                         │
        ▼ (Đúng)                                  ▼ (Cắt ảnh)
 1. Lưu file vào `active_gif.gif`          1. Mở thư viện `UCrop` (Tỷ lệ 9:16)
 2. Lưu pref `selected_gif_path`           2. Lưu kết quả cắt vào `active_static_wallpaper.jpg`
    và `gif_updated_at`                    3. Hiển thị Dialog chọn Màn hình
 3. Khởi chạy `LiveWallpaperService`        4. Gọi `WallpaperManager.setStream()`
 4. Chuyển về MainActivity                 5. Chuyển về MainActivity
```

---

### 🌐 LUỒNG 5: CÀI HÌNH NỀN TỪ DẪN LINK URL BẤT KỲ (Url Wallpaper Flow)

Cho phép người dùng nhập trực tiếp một đường link ảnh công khai trên Internet để đặt làm hình nền.

* **Màn hình**: `UrlWallpaperFragment`
* **Quy trình**:
  1. Người dùng nhập URL -> Nhấn "Preview" -> Glide tải hiển thị lên `ivPreview`.
  2. Nhấn "Set Wallpaper" -> `UrlWallpaperViewModel.setWallpaperFromUrl(url)`.
  3. Gọi `DownloadImageToBitmapUseCase(url)` dùng Glide tải ảnh dạng `Bitmap`.
  4. Gọi `SetWallpaperUseCase(bitmap)` sử dụng `WallpaperManager.setBitmap(bitmap)` để cập nhật hình nền.

---

### ❤️ LUỒNG 6: QUẢN LÝ DANH SÁCH YÊU THÍCH (Favorite Wallpapers Flow)

* **Thêm / Xóa Yêu thích**:
  * Tại `WallpaperDetailFragment`, người dùng bấm tim (`fabFavorite`).
  * Thực thi `ToggleFavoriteUseCase` -> Cập nhật trường `isFavorite` của `WallpaperEntity` trong **Room Database**.
  * Phát hiệu ứng nảy tim (ScaleAnimation với `OvershootInterpolator`).
* **Hiển thị danh sách Yêu thích**:
  * Màn hình `FavoriteFragment` gọi `GetFavoriteWallpapersUseCase()`.
  * Trả về `Flow<Result<List<WallpaperDtoModel>>>` trực tiếp từ `WallpaperDao`.
  * Hiển thị lên RecyclerView. Danh sách này cũng chính là **nguồn dữ liệu đầu vào** cho `AutoWallpaperWorker`.

---

### 🔍 LUỒNG 7: TÌM KIẾM VÀ KHÁM PHÁ THEO DANH MỤC (Explore & Search Flow)

* **Màn hình chính (`HomeFragment`)**:
  * Hiển thị Carousel **Featured Wallpapers** và danh sách **Top Wallpapers**.
  * Lấy danh sách Categories (Nature, Technology, Animals, Abstract,...).
* **Tìm kiếm (`SearchFragment`)**:
  * Nhập từ khóa -> Trừ khử tìm kiếm qua `SearchWallpapersUseCase`.
  * Thực thi truy vấn SQL `LIKE %query%` trong `WallpaperDao`.
  * Giao diện sơ đồ danh sách gợi ý (`rvSuggestions`) được căn chỉnh khớp hoàn toàn theo viền khung nhập tìm kiếm (`edtSearch`).
* **Chi tiết danh mục (`CategoryDetailFragment`)**:
  * Lọc danh sách hình nền thuộc danh mục được chọn qua `GetWallpapersByCategoryUseCase`.

---

### 🚀 LUỒNG 8: KHỞI ĐỘNG VÀ ONBOARDING (Splash & Onboard Flow)

* **Splash (`SplashFragment`)**:
  * Kiểm tra trạng thái lần đầu mở ứng dụng (`GetIsFirstLaunchUseCase`), cấu hình Remote Config (`AppRemoteConfig`).
  * Tự động chuyển hướng đến `OnboardFragment` hoặc `HomeFragment` sau 2 giây.
* **Onboarding (`OnboardFragment`)**:
  * Gồm 5 màn hình giới thiệu tính năng ứng dụng sử dụng `ViewPager2` và `OnboardFragmentStateAdapter`.
  * Khi hoàn tất, cập nhật trạng thái `setIsFirstLaunch(false)` và điều hướng vào màn hình chính `HomeFragment`.

---

### 🌐 LUỒNG 9: THAY ĐỔI NGÔN NGỮ (Language Flow)

* **Màn hình**: `LanguageFragment`, `ChangeLanguageFragment`.
* **Quy trình**:
  * Hiển thị danh sách ngôn ngữ hỗ trợ qua `GetLanguagesUseCase` và `LanguageAdapter`.
  * Người dùng chọn ngôn ngữ -> Lưu vào DataStore thông qua `SetLanguageUseCase`.
  * Gọi `LanguageManager.setLocale()` để cập nhật cấu hình ngôn ngữ ứng dụng (`Configuration` & `Context`) và khởi động lại `MainActivity` áp dụng ngay lập tức.

---

## 🛠️ TỔNG HỢP BASE CLASSES & CONVENTIONS SỬ DỤNG TRONG CÁC LUỒNG

| Tên Base / Pattern | Mô tả & Quy định bắt buộc |
|---|---|
| **BaseFragment** | Tất cả Fragment kế thừa `BaseFragment<Binding, ViewModel>`. Cung cấp sẵn `navigator`, `showHideLoading()`, `collectFlowOnView()`. |
| **BaseViewModel** | Quản lý state tập trung dạng `StateFlow<UiState>` và sự kiện một lần dạng `Channel<Event>`. |
| **UseCase Pattern** | Mỗi UseCase chỉ làm đúng 1 nhiệm vụ duy nhất (`operator fun invoke`). ViewModel **không** gọi trực tiếp Repository. |
| **Coroutines Handling** | Sử dụng `launchIO { }`, `launchMain { }` thay cho `lifecycleScope.launch`. |
| **Api/Result Extension** | Dùng `handleApiCall()` để tự động catch exception và emit trạng thái UI Clean. |

---
*Tài liệu được cập nhật mới nhất cho dự án Pion-Base.*
