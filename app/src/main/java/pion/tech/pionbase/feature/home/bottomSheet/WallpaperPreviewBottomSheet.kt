package pion.tech.pionbase.feature.home.bottomSheet

import android.net.Uri
import android.view.View
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.databinding.BottomSheetPreviewWallpaperBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class WallpaperPreviewBottomSheet(
    private val uri: Uri,
    private val onConfirm: (Uri) -> Unit
) : BaseBottomSheetDialogFragment<BottomSheetPreviewWallpaperBinding>(
    BottomSheetPreviewWallpaperBinding::inflate
) {

    override fun initView(savedInstanceState: android.os.Bundle?) {
        super.initView(savedInstanceState)
        binding.imgPreview.setImageURI(uri)
        
        // Lấy tên file từ URI
        val fileName = uri.lastPathSegment ?: "Wallpaper"
        binding.tvImageName.text = fileName
    }

    override fun addEvent(savedInstanceState: android.os.Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnSetWallpaper.setPreventDoubleClickScaleView {
            onConfirm(uri)
            dismiss()
        }
    }
}
