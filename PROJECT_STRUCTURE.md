# TÀI LIỆU CẤU TRÚC THƯ MỤC VÀ CHỨC NĂNG CÁC THƯ MỤC (PROJECT STRUCTURE & FOLDER FUNCTIONS) - PION-BASE

Tài liệu này mô tả chi tiết chức năng của từng thư mục và thư mục con trong cấu trúc mã nguồn gói `pion.tech.pionbase` của dự án **Pion-Base**.

---

## 📂 SƠ ĐỒ TỔNG QUAN CẤU TRÚC GÓI (PACKAGE STRUCTURE)

```text
pion.tech.pionbase
├── app/               # Các thành phần cốt lõi khởi chạy và quản lý vòng đời ứng dụng cấp cao
├── base/              # Các lớp cơ sở (Base Classes) bắt buộc và tiện ích UI chung
├── data/              # Lớp dữ liệu (Data Layer): Database, Models, Remote, Repositories
├── di/                # Cấu hình Dependency Injection (Koin Modules)
├── domain.usecase/    # Lớp nghiệp vụ (Domain Layer): UseCases đơn nhiệm vụ
├── feature/           # Lớp giao diện theo từng tính năng (Feature-first Package)
├── service/           # Các dịch vụ nền (Foreground/Background Services: Live Wallpaper, Video)
├── util/              # Các lớp tiện ích chung (Extensions, Helpers, Constants)
└── worker/            # Các tác vụ chạy ngầm định kỳ bằng WorkManager
```

---

## 📁 1. THƯ MỤC `app/` (Application Core)
Chứa các thành phần khởi tạo ứng dụng (`Application`), Activity chính (`MainActivity`) và các ViewModel toàn cục hoặc điều phối API cấp app.
* **`MyApplication.kt`**: Lớp `Application` chính của ứng dụng, chịu trách nhiệm khởi tạo Koin DI, Timber logging, Firebase, và các thư viện toàn cục.
* **`MainActivity.kt`**: Activity duy nhất trong kiến trúc Single-Activity, chứa `NavHostFragment` và điều phối điều hướng chung.
* **`CommonViewModel.kt`**: ViewModel dùng chung cho toàn bộ ứng dụng (chia sẻ trạng thái như dữ liệu premium, danh mục chung qua `activityViewModel()`).
* **`ApiViewModel.kt`**: ViewModel quản lý các trạng thái hoặc gọi API dùng chung toàn app.

---

## 📁 2. THƯ MỤC `base/` (Base Classes & Framework)
Chứa các khung cơ sở chuẩn hóa toàn dự án để tuân thủ nguyên tắc thiết kế sạch và đồng bộ.
* **`BaseFragment.kt`**: Lớp cơ sở cho mọi Fragment (quản lý ViewBinding tự động, vòng đời, `showHideLoading`, `navigator`, coroutine launchers).
* **`BaseViewModel.kt`**: Lớp cơ sở cho mọi ViewModel (quản lý `StateFlow` cho UI State và `Channel` cho One-shot Events).
* **`BaseDialogFragment.kt` & `BaseBottomSheetDialogFragment.kt`**: Lớp cơ sở chuẩn hóa cho các Dialog và BottomSheet.
* **`BaseListAdapter.kt`**: Lớp cơ sở cho `ListAdapter` sử dụng `DiffUtil` giúp tối ưu hiệu năng hiển thị RecyclerView.
* **`navigator/`**: Giao diện (`Navigator`) và cài đặt (`NavigatorImpl`) xử lý điều hướng an toàn qua Navigation Component (`R.id.action_...`).
* **`firebaseAnalytics/`**: Các trình ghi log sự kiện, chuẩn hóa tên sự kiện cho Firebase Analytics.
* **`lifecycleCallback/`**: Lắng nghe vòng đời của Activity và Fragment để debug hoặc xử lý chung.

---

## 📁 3. THƯ MỤC `data/` (Data Layer)
Đảm nhiệm việc giao tiếp với nguồn dữ liệu ngoài (API, Room Database, Local DataStore) và định nghĩa các mô hình dữ liệu.
* **`database/`**: Chứa Room Database (`AppDatabase.kt`) và các Data Access Objects (`dao/`: `WallpaperDao`, `CategoryDao`, `DummyDao`).
* **`model/`**: Chứa các mô hình dữ liệu phân chia rõ ràng:
  * `*DtoModel.kt`: Dữ liệu thô từ API hoặc cơ sở dữ liệu.
  * `*UIModel.kt`: Dữ liệu đã ánh xạ (`toPresentation()`) phục vụ riêng cho UI Layer.
* **`remote/`**: Cấu hình Retrofit `ApiInterface`, OkHttp `HeaderInterceptor` và các DataSource từ mạng.
* **`repository/`**: Triển khai Repository pattern (Interface + Impl) kết nối dữ liệu từ Local/Remote về Domain Layer, bao gồm:
  * `wallpaperRepository/`: Quản lý dữ liệu hình nền, danh mục, yêu thích.
  * `dataStoreRepository/`: Đọc ghi SharedPreferences / Jetpack DataStore (cài đặt app, ngôn ngữ, auto-change).
  * `languageRepository/`, `installedAppRepository/`, `apiRepository/`.

---

## 📁 4. THƯ MỤC `di/` (Dependency Injection - Koin)
Chứa toàn bộ các module Koin để cấu hình dependency injection cho toàn bộ dự án.
* **`AppModule.kt`**: Điểm tập trung gom nhóm tất cả các module Koin (`coreModule`, `networkModule`, `databaseModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`, `workerModule`).
* **`CoreModule.kt`**: Đăng ký DataStore, RemoteConfig.
* **`NetworkModule.kt`**: Cấu hình Retrofit, OkHttpClient, Gson.
* **`DatabaseModule.kt`**: Đăng ký Room Database và các DAO.
* **`RepositoryModule.kt`**: Ràng buộc (bind) các Repository Impl vào Interface tương ứng.
* **`UseCaseModule.kt`**: Đăng ký tự động các UseCase dạng `factoryOf(...)`.
* **`ViewModelModule.kt`**: Đăng ký các ViewModel dạng `viewModelOf(...)`.
* **`PlatformModule.kt` & `WorkerModule.kt`**: Đăng ký Firebase Analytics và WorkManager factories.

---

## 📁 5. THƯ MỤC `domain.usecase/` (Domain Layer - Business Logic)
Chứa các UseCase thực hiện các nghiệp vụ độc lập, mỗi UseCase chỉ làm đúng **một nhiệm vụ duy nhất** (`operator fun invoke`).
* **`api/`**: Các UseCase gọi API lấy danh mục, template dữ liệu mẫu.
* **`base/`**: `BaseUseCase` định nghĩa khung chuẩn cho các UseCase trả về luồng `Flow<Result<T>>`.
* **`common/`**: UseCase quản lý trạng thái chung (ví dụ: `GetIsPremiumUseCase`, `SetIsPremiumUseCase`).
* **`home/`**: Các UseCase phục vụ màn hình chính, xử lý chuyển đổi hình nền, tải ảnh về bitmap.
* **`language/`**: Các UseCase quản lý ngôn ngữ và trạng thái khởi chạy lần đầu (`GetLanguageUseCase`, `SetLanguageUseCase`,...).
* **`settings/`**: UseCase lấy/lưu cấu hình tự động đổi hình nền (`GetAutoWallpaperSettingsUseCase`,...).
* **`wallpaper/`**: Các UseCase xử lý hình nền (lấy danh sách top, featured, favorite, category, tìm kiếm, toggle yêu thích).

---

## 📁 6. THƯ MỤC `feature/` (Presentation Feature Modules)
Được tổ chức theo từng tính năng riêng biệt (Feature-first Architecture). Mỗi thư mục con bao gồm Fragment, FragmentEx (UI extension), ViewModel và các adapter/dialog liên quan:
* **`home/`**: Màn hình chính của ứng dụng, chứa danh mục, hình nền nổi bật, top hình nền, và `EditWallpaperActivity`.
* **`wallpaperDetail/`**: Màn hình chi tiết hình nền, xử lý xem phóng to, tải về máy (`Download`), và đặt làm hình nền (`Set Wallpaper`).
* **`favorite/`**: Màn hình hiển thị danh sách hình nền đã thích (Favorite).
* **`search/`**: Màn hình tìm kiếm hình nền theo từ khóa với danh sách gợi ý (`SuggestionAdapter`).
* **`categoryDetail/`**: Màn hình hiển thị chi tiết hình nền theo từng danh mục được chọn.
* **`setting/`**: Màn hình cài đặt ứng dụng (bật/tắt tự động đổi hình nền, chọn thư viện ảnh cá nhân, thông tin dev).
* **`language/` & `changeLanguage/`**: Các màn hình chọn ngôn ngữ khi mới mở app hoặc thay đổi trong cài đặt.
* **`onboard/`**: Màn hình giới thiệu hướng dẫn sử dụng ứng dụng khi mở lần đầu (Onboarding với `ViewPager2`).
* **`splash/`**: Màn hình chờ khởi động, kiểm tra cấu hình ban đầu và điều hướng.
* **`urlWallpaper/`**: Màn hình cho phép người dùng nhập URL trực tiếp để đặt làm hình nền.

---

## 📁 7. THƯ MỤC `service/` (Background & Live Wallpaper Services)
Chứa các dịch vụ Android chuyên dụng để chạy hình nền động hoặc xử lý nền.
* **`LiveWallpaperService.kt`**: Dịch vụ Live Wallpaper (`WallpaperService`) dùng `android.graphics.Movie` để render và phát hình nền động dạng GIF trên luồng phụ mượt mà.
* **`VideoWallpaperService.kt`**: Dịch vụ phát hình nền động dạng Video sử dụng `MediaPlayer` gắn kết với `SurfaceView`.

---

## 📁 8. THƯ MỤC `util/` (Utilities & Helpers)
Chứa các tệp tiện ích mở rộng (Extensions) và các lớp trợ giúp dùng chung trong ứng dụng.
* **`ViewExtensions.kt`**: Các hàm mở rộng cho View (chống double click `setPreventDoubleClick`, ẩn/hiện view, scale animation,...).
* **`ApiExtensions.kt`**: Các hàm mở rộng `handleApiCall()` để xử lý gọi API/UseCase tự động bắt lỗi và quản lý StateFlow.
* **`DataStoreExtensions.kt`**: Tiện ích đọc ghi nhanh DataStore.
* **`PermissionUtils.kt`**: Kiểm tra và yêu cầu quyền hệ thống (ví dụ quyền lưu trữ).
* **`GlideConfig.kt`**: Cấu hình Glide cho việc tải ảnh.
* **`Constant.kt` & `BundleKey.kt`**: Khai báo các hằng số và key truyền dữ liệu Intent/Bundle.
* **`DeviceDimensionsHelper.kt`, `KeyboardEx.kt`, `MimeTypeExtensions.kt`, `ParcelableEx.kt`, `Utils.kt`**: Các tiện ích hỗ trợ định dạng, đo đạc kích thước màn hình và xử lý bàn phím.

---

## 📁 9. THƯ MỤC `worker/` (Background WorkManager Tasks)
Chứa các tác vụ chạy ngầm định kỳ bằng Jetpack WorkManager.
* **`AutoWallpaperWorker.kt`**: Worker định kỳ thực hiện việc lấy ngẫu nhiên hình nền từ danh sách yêu thích và tự động thay đổi hình nền hệ thống theo khoảng thời gian người dùng cấu hình trong Settings.

---
*Tài liệu cấu trúc thư mục được tổng hợp theo mã nguồn thực tế của dự án Pion-Base.*
