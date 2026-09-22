# Implement Set Wallpaper from URL

Cho phép người dùng nhập URL ảnh tại màn hình Home, hiển thị ảnh và đặt làm hình nền điện thoại.

## User Review Required

- Yêu cầu thêm quyền SET_WALLPAPER vào AndroidManifest.xml.
- Sử dụng WallpaperManager API của Android.
- Hình ảnh sẽ được load qua Glide.

## Proposed Changes

### [Domain Layer]
#### [NEW] [DownloadImageToBitmapUseCase.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/domain/usecase/home/DownloadImageToBitmapUseCase.kt)
#### [NEW] [SetWallpaperUseCase.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/domain/usecase/home/SetWallpaperUseCase.kt)

### [Data Layer / DI]
#### [MODIFY] [AppModule.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/di/AppModule.kt)
- Đăng ký DownloadImageToBitmapUseCase và SetWallpaperUseCase.

### [UI Layer]
#### [MODIFY] [FragmentHomeBinding.xml](file:///E:/Pion-Base/app/src/main/res/layout/fragment_home.xml)
- Thêm EditText, ImageView, Button.
#### [MODIFY] [HomeViewModel.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/home/HomeViewModel.kt)
- Thêm logic xử lý.
#### [MODIFY] [HomeFragmentEx.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/home/HomeFragmentEx.kt)
- Xử lý sự kiện click.

## Verification Plan

### Manual Verification
- Kiểm tra nhập URL, hiển thị ảnh bằng Glide, đặt làm hình nền.
