# Phân tích lỗi Chức năng Ngôn ngữ

Hiện tại `LanguageViewModel` đã cập nhật `selectedLanguage` và `isSelected` trong `LanguageUIModel` thông qua `getSelectedLanguageListUiState()`, nhưng `Adapter` dường như không cập nhật giao diện (UI) ngay sau khi chọn.

## Vấn đề xác định:
1. `onClickLanguage` trong `LanguageFragment` chỉ update `viewModel.selectLanguage(item)`.
2. `viewModel` cập nhật trạng thái, `LanguageUiState` thay đổi.
3. `LanguageFragment` quan sát `uiState` thông qua `collectFlowOnView` và gọi `adapter.submitList(languages)`.
4. `LanguageAdapter` sử dụng `createDiffCallback` để so sánh các item.
   Nếu `areContentsTheSame` trả về `true` (do item được `copy` đúng nhưng vẫn có thể có issue với `DiffCallback`), `RecyclerView` có thể không chạy lại `onBindViewHolder`.

### Kế hoạch sửa lỗi:
1. Kiểm tra lại `areContentsTheSame` trong `LanguageAdapter`.
2. Thêm phương thức `notifyDataSetChanged()` hoặc kiểm tra lại logic so sánh.
3. Đảm bảo ngôn ngữ được lưu lại vào `DataStore` (hiện tại `viewModel` có gọi `setFirstLaunchFalse` nhưng không thấy lưu ngôn ngữ).
