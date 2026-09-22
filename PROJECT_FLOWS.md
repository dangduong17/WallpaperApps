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

Luồng xử lý cài đặt hình nền từ màn hình chi tiết (`WallpaperDetailFragment`), phân biệt giữa **Hình nền Tĩnh (Static Image)** và **Hình nền Động (GIF)**.

```
[WallpaperDetailFragment] ──► Người dùng nhấn "Set Wallpaper"
                                          │
                     ┌────────────────────┴────────────────────┐
                     │                                         │
               [Ảnh Tĩnh (Static)]                     [Ảnh Động (GIF)]
                     │                                         │
                     ▼                                         ▼
   Hiển thị Dialog chọn Màn hình              Mở EditWallpaperActivity
   (Màn chính / Màn khóa / Cả hai)                           │
                     │                                         ▼
                     ▼                       Sao chép file vào internal storage:
   `WallpaperDetailViewModel.applyWallpaper()`   `filesDir/active_gif.gif`
                     │                                         │
                     ▼                                         ▼
           [SetWallpaperUseCase]              Lưu đường dẫn & timestamp vào Prefs:
                     │                        `selected_gif_path`, `gif_updated_at`
                     ▼                                         │
  `WallpaperManager.setStream()` hoặc                         ▼
   `WallpaperManager.setBitmap()`            Khởi chạy Intent cài đặt Live Wallpaper:
                     │                        `WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`
                     ▼                        Trỏ tới `LiveWallpaperService`
     Hiển thị Snackbar thành công                              │
                                                               ▼
                                                  [LiveWallpaperService]
                                                  - Chạy luồng vẽ phụ HandlerThread
                                                  - Tải GIF qua android.graphics.Movie
                                                  - Lắng nghe Prefs & File timestamp
                                                    để nạp lại GIF mới tức thì
```

#### Chi tiết kỹ thuật:
1. **Hình nền Tĩnh**: Sử dụng `WallpaperManager` gốc của Android truyền `FLAG_SYSTEM`, `FLAG_LOCK`, hoặc kết hợp cả hai.
2. **Hình nền GIF (Live Wallpaper)**:
   * Chuyển hướng qua `EditWallpaperActivity`.
   * Ghi nội dung file GIF vào vùng bộ nhớ riêng của ứng dụng (`filesDir/active_gif.gif`).
   * Cập nhật SharedPreferences `wallpaper_prefs` lưu `selected_gif_path` và `gif_updated_at`.
   * Gọi `LiveWallpaperService` (kế thừa từ `android.service.wallpaper.WallpaperService`). Engine `GifWallpaperEngine` sử dụng **`android.graphics.Movie`** (Native GIF Decoder) chạy trên luồng phụ **`HandlerThread("GifWallpaperThread")`** độc lập với Main UI Thread, đảm bảo không bao giờ đơ/lag ứng dụng hay bị ANR ("App buộc dừng").
   * Lắng nghe sự kiện qua `OnSharedPreferenceChangeListener` và kiểm tra thời gian cập nhật file (`file.lastModified()`) để tự động nạp hình GIF mới tức thì khi người dùng thay đổi hình nền.

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
        ▼ (Đúng)                                  ▼ (Khái niệm Cắt Ảnh)
 1. Lưu file vào `active_gif.gif`          1. Mở thư viện `UCrop` (Tỷ lệ 9:16)
 2. Lưu pref `selected_gif_path`           2. Lưu kết quả cắt vào `active_static_wallpaper.jpg`
    và `gif_updated_at`                    3. Hiển thị Dialog chọn Màn hình
 3. Khởi chạy `LiveWallpaperService`        4. Gọi `WallpaperManager.setStream()`
 4. Chuyển về MainActivity                 5. Chuyển về MainActivity
```

#### Chi tiết xử lý UCrop & Service:
* Khi là **Ảnh tĩnh**: Ứng dụng tự động ép tỷ lệ cắt chuẩn màn hình điện thoại **9:16** thông qua thư viện `UCrop`.
* Sau khi đặt hình nền thành công, `EditWallpaperActivity` quay về `MainActivity` và gửi cờ `show_success_msg = true` để thông báo cho người dùng.

---

## ➕ 3. BỔ SUNG CÁC LUỒNG QUAN TRỌNG KHÁC TRONG DỰ ÁN

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

### 🎬 LUỒNG 8: MÔ-ĐUN VIDEO WALLPAPER SERVICE (Video Wallpaper Flow)

* **Service**: `VideoWallpaperService` (kế thừa `WallpaperService`).
* **Nguyên lý**:
  * Lấy đường dẫn video từ SharedPreferences (`wallpaper_path`).
  * Khởi tạo `MediaPlayer`, gắn mặt phẳng hiển thị `setSurface(holder.surface)` và đặt `isLooping = true`.
  * Quản lý vòng đời phát Video: Tự động `start()` khi màn hình hiển thị (`onVisibilityChanged(true)`) và `pause()` khi ẩn màn hình (`onVisibilityChanged(false)`) để tiết kiệm pin tối đa.

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
*Tài liệu được tổng hợp tự động từ mã nguồn dự án Pion-Base.*
