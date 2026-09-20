# Triển khai hỗ trợ GIF và ảnh từ máy người dùng

## Mục tiêu
Cho phép người dùng chọn ảnh/GIF từ thư viện máy, hiển thị đúng định dạng (tĩnh/động) bằng Glide và hỗ trợ cài đặt làm hình nền.

## User Review Required
- **[IMPORTANT]**: Cần xác nhận phương thức cài đặt hình nền cho GIF. Mặc định `WallpaperManager` không hỗ trợ GIF. Hiện tại chỉ dừng lại ở mức **hiển thị trong ứng dụng**.

## Proposed Changes

### [Feature: WallpaperDetail]
#### [MODIFY] [WallpaperDetailFragment.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/wallpaperDetail/WallpaperDetailFragment.kt)
- Cập nhật logic `subscribeObserver` để sử dụng `Glide` hỗ trợ `.asGif()`.
- Kiểm tra kiểu dữ liệu của `wallpaperUri` để quyết định load ảnh.

#### [MODIFY] [WallpaperDetailViewModel.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/wallpaperDetail/WallpaperDetailViewModel.kt)
- Cập nhật UI State để lưu trữ loại nội dung (IMAGE/GIF).

## Verification Plan

### Automated Tests
- Kiểm tra hiển thị ảnh tĩnh.
- Kiểm tra hiển thị GIF (nếu có tệp test).

### Manual Verification
- Deploy app và chọn ảnh/GIF từ gallery.
- Xác nhận ảnh tĩnh hiển thị bình thường.
- Xác nhận GIF hiển thị chuyển động.
