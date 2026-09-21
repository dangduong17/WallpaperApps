package pion.tech.pionbase.feature.home.bottomSheet

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.databinding.BottomSheetPreviewWallpaperBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import java.io.File

class WallpaperPreviewBottomSheet(
    private val uri: Uri,
    private val onConfirm: (Uri) -> Unit
) : BaseBottomSheetDialogFragment<BottomSheetPreviewWallpaperBinding>(
    BottomSheetPreviewWallpaperBinding::inflate
) {

    companion object {
        private const val DEFAULT_IMAGE_NAME = "Wallpaper"
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        Glide.with(this)
            .load(uri)
            .into(binding.imgPreview)
        binding.tvImageName.text = uri.lastPathSegment ?: DEFAULT_IMAGE_NAME
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnSetWallpaper.setPreventDoubleClickScaleView {
            onConfirm(uri)
            dismiss()
        }
    }

}
