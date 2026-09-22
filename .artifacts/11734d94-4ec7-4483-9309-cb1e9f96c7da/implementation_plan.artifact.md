# Triển khai Live Wallpaper Service cho GIF

## Mục tiêu
Cho phép người dùng chọn GIF từ thư viện và cài đặt làm hình nền động (Live Wallpaper) cho màn hình chính thông qua `WallpaperService`.

## User Review Required
- **[IMPORTANT]**: Cần giải thích cho người dùng: App sẽ cần đăng ký một `WallpaperService` và người dùng phải chọn app của bạn từ danh sách "Live Wallpaper" của hệ thống sau khi nhấn "Set".

## Open Questions
- Bạn có muốn lưu trữ URI của GIF hiện tại để Service tự động load không, hay sẽ dùng một cách tiếp cận khác (ví dụ: copy file vào app directory)? *Tạm thời: Sử dụng URI được lưu vào `DataStore` để Service có thể truy cập.*

## Proposed Changes

### [Feature: WallpaperService]
#### [NEW] [LiveWallpaperService.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/service/LiveWallpaperService.kt)
- Triển khai `WallpaperService` sử dụng Glide để load GIF vào `Canvas` của `WallpaperService.Engine`.

#### [NEW] [wallpaper_service_config.xml](file:///E:/Pion-Base/app/src/main/res/xml/wallpaper_service_config.xml)
- File cấu hình cho dịch vụ hình nền.

#### [MODIFY] [AndroidManifest.xml](file:///E:/Pion-Base/app/src/main/AndroidManifest.xml)
- Đăng ký `LiveWallpaperService`.

### [Feature: Home]
#### [MODIFY] [HomeFragment.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/home/HomeFragment.kt)
- Cập nhật hàm `showWallpaperOptionsDialog` để thêm lựa chọn "Hình nền động (Live Wallpaper)".

## Verification Plan

### Manual Verification
- Nhấn chọn GIF -> Xem Preview (đã động).
- Nhấn "Set" -> Chọn "Hình nền động".
- Hệ thống mở màn hình Preview của hệ thống -> Nhấn "Áp dụng".
- Quay ra màn hình chính kiểm tra GIF có động không.
