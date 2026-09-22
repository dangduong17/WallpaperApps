# Debug tiếp: Adapter bị reset sai cách?

Khi `Fragment` quay lại từ backstack, `onViewCreated` gọi `init()` -> `initView()`.
Trong `initView()` của `CategoryDetailFragmentEx.kt`:
```kotlin
fun CategoryDetailFragment.initView() {
    binding.tvTitle.text = args.categoryName

    adapter.setListener(this)
    binding.rvWallpapers.adapter = adapter // <--- Dòng này có thể là vấn đề
}
```
Việc set `binding.rvWallpapers.adapter = adapter` lại mỗi lần quay lại có thể gây mất focus hoặc reset trạng thái của `RecyclerView` (dù `adapter` là biến instance, việc gán lại có thể trigger lại layout pass).

## Giải pháp:
Thay vì gán lại `adapter` trong `initView()` mỗi lần quay lại, chỉ cần gán nếu `rvWallpapers.adapter == null`.

## Kế hoạch:
1. Sửa `initView` trong cả `CategoryDetailFragmentEx.kt` và `FavoriteFragmentEx.kt`.
2. Kiểm tra log để xem event có thực sự được gọi nhưng bị chặn ở đâu đó không.
3. Nếu không được, xem xét `NavigatorImpl` có giữ lại `navObserver` gây xung đột không.
