package pion.tech.pionbase.feature.home.bottomSheet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.yalantis.ucrop.UCrop
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

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        com.bumptech.glide.Glide.with(this)
            .load(uri)
            .into(binding.imgPreview)
        binding.tvImageName.text = uri.lastPathSegment ?: "Wallpaper"
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnSetWallpaper.setPreventDoubleClickScaleView {
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_wallpaper_" + System.currentTimeMillis() + ".gif"))
            UCrop.of(uri, destinationUri)
                .withAspectRatio(9f, 16f)
                .start(requireActivity(), this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                onConfirm(resultUri)
                dismiss()
            }
        }
    }
}
