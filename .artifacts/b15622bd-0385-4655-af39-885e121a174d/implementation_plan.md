# Triển khai tính năng Set Live Wallpaper (Video/GIF)

Triển khai tính năng đặt Video/GIF làm hình nền động thông qua `WallpaperService`.

## User Review Required

- Tôi sẽ tạo mới một `WallpaperService` để xử lý việc render hình nền.
- Cần quyền `android.permission.BIND_WALLPAPER` trong Manifest.
- Sử dụng `WallpaperManager` để kích hoạt giao diện chọn hình nền của hệ thống.

## Open Questions

- Bạn đã có thư viện cụ thể nào để load GIF/Video làm wallpaper chưa (vd: ExoPlayer cho Video, Glide cho GIF)? Nếu chưa, tôi sẽ dùng `MediaPlayer` tiêu chuẩn cho video.

## Proposed Changes

### [Service Layer]
- [NEW] `pion/tech/pionbase/service/VideoWallpaperService.kt`
- [NEW] `app/src/main/res/xml/wallpaper.xml`

### [UI Layer]
- [MODIFY] `app/src/main/AndroidManifest.xml` (Thêm service)
- [MODIFY] `pion/tech/pionbase/feature/wallpaperDetail/WallpaperDetailFragmentEx.kt` (Kích hoạt Intent chuyển sang Live Wallpaper)

## Verification Plan

### Manual Verification
- Chạy app, vào màn hình Detail của wallpaper, nhấn nút "Set as Wallpaper".
- Kiểm tra xem hệ thống có hiển thị màn hình Live Wallpaper Preview không.
- Xác nhận áp dụng thành công.
