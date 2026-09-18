# Kế hoạch phân tích lỗi crash khi tải ảnh

Mục tiêu là tìm ra nguyên nhân gây crash ứng dụng khi hiển thị ảnh, dựa trên việc kiểm tra cách sử dụng Glide trong dự án.

## User Review Required

- Việc phân tích này dựa trên các đoạn code hiện tại. Nếu bạn có stack trace từ Logcat, vui lòng cung cấp để tôi xác định chính xác vị trí crash.

## Open Questions

- Bạn đã thấy lỗi này xuất hiện ở màn hình cụ thể nào chưa?
- Crash xảy ra ngay khi mở ảnh hay sau một khoảng thời gian?

## Proposed Changes

### [Phân tích & Kiểm tra]

- Kiểm tra lại các file sử dụng `ImageView.loadImage` và `ImageView.loadWithCallback` trong `ViewExtensions.kt`.
- Kiểm tra lại các file layout có sử dụng `ImageView` để xem có thiết lập sai thuộc tính nào gây crash (ví dụ: ảnh quá lớn, lỗi bộ nhớ).
- Kiểm tra các phần xử lý Callback trong `loadWithCallback` xem có nguy cơ gây `NullPointerException` hoặc `IllegalStateException` không.

## Verification Plan

### Automated Tests
- Kiểm tra xem có test case nào liên quan đến hiển thị ảnh không.

### Manual Verification
- Bạn sẽ giúp tôi kiểm tra lại sau khi tôi đưa ra các đề xuất thay đổi code.
