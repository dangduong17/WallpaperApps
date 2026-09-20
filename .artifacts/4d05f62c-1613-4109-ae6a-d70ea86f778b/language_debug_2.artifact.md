# Phân tích chức năng Ngôn ngữ

Vấn đề bạn gặp phải (ngôn ngữ không đổi) có hai nguyên nhân chính:
1. **Dữ liệu**: Chưa lưu ngôn ngữ vào `DataStore` (tôi đã phát hiện ra `DataStoreRepository` chưa có hàm `setLanguage` hoặc `getLanguage`).
2. **Logic thực thi**: Việc chọn một ngôn ngữ trong giao diện không tự động gọi API hay `Locale.setDefault()` để thay đổi ngôn ngữ của toàn bộ ứng dụng Android.

## Kế hoạch sửa lỗi:
1. Thêm `getLanguage` và `setLanguage` vào `DataStoreRepository` và `DataStoreRepositoryImpl`.
2. Tạo/Cập nhật một `LanguageManager` hoặc sử dụng `AppCompatDelegate` để cập nhật ngôn ngữ của ứng dụng.
3. Cập nhật `LanguageViewModel` để lưu vào `DataStore` và kích hoạt thay đổi.
4. Cập nhật `LanguageFragment` để khởi động lại Activity hoặc cập nhật UI ngay lập tức khi đổi ngôn ngữ.

---
Bạn có muốn tôi bắt đầu triển khai các thay đổi này không? (Vì đây là thay đổi kiến trúc nhỏ liên quan đến DataStore và Locale của hệ thống, tôi muốn xác nhận trước).
