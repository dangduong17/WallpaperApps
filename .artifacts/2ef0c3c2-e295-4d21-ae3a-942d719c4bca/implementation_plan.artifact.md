# Refactor MainActivity để tuân thủ kiến trúc MVVM

Di chuyển việc truy cập `DataStoreRepository` trong `MainActivity` vào `CommonViewModel` để tuân thủ nguyên tắc "ViewModel là trung gian duy nhất giữa View và Data Layer".

## User Review Required

- Tôi sẽ di chuyển logic khởi tạo ngôn ngữ (`LanguageManager.init`) từ `MainActivity` vào `CommonViewModel`. Điều này giúp `MainActivity` không cần inject trực tiếp `DataStoreRepository` nữa.

## Proposed Changes

### ViewModel & Data Layer

#### [MODIFY] [CommonViewModel.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/app/CommonViewModel.kt)
- Thêm tham số `context` hoặc logic phù hợp để gọi `LanguageManager.init` bên trong `CommonViewModel`.

#### [MODIFY] [MainActivity.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/app/MainActivity.kt)
- Loại bỏ `private val dataStoreRepository: DataStoreRepository by inject()`.
- Loại bỏ dòng `LanguageManager.init(this, dataStoreRepository)` trong `onCreate`.

## Verification Plan

### Manual Verification
- Kiểm tra app vẫn khởi chạy bình thường.
- Kiểm tra cài đặt ngôn ngữ vẫn hoạt động đúng sau khi thay đổi.
