package pion.tech.pionbase.feature.home.bottomSheet

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.databinding.BottomSheetPreviewWallpaperBinding
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import java.io.File

class WallpaperPreviewBottomSheet : BaseBottomSheetDialogFragment<BottomSheetPreviewWallpaperBinding>(
    BottomSheetPreviewWallpaperBinding::inflate
) {

    private var uri: Uri? = null
    private var onConfirm: ((Uri) -> Unit)? = null

    companion object {
        private const val ARG_URI = "arg_uri"
        
        fun newInstance(uri: Uri, onConfirm: (Uri) -> Unit): WallpaperPreviewBottomSheet {
            return WallpaperPreviewBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
                this.onConfirm = onConfirm
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        uri = arguments?.getParcelable(ARG_URI)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        uri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.imgPreview)
            // binding.tvImageName.text = it.lastPathSegment ?: "Wallpaper" // Removed as requested
        }
    }

    override fun addEvent(savedInstanceState: Bundle?) {
        super.addEvent(savedInstanceState)
        binding.btnSetWallpaper.setPreventDoubleClickScaleView {
            uri?.let { uri ->
                onConfirm?.invoke(uri)
                dismiss()
            }
        }
    }
}
