package pion.tech.pionbase.feature.wallpaperDetail

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import android.view.animation.Animation
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentWallpaperDetailBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

class WallpaperDetailFragment : BaseFragment<FragmentWallpaperDetailBinding, WallpaperDetailViewModel>(
    FragmentWallpaperDetailBinding::inflate,
    WallpaperDetailViewModel::class
) {
    private val args: WallpaperDetailFragmentArgs by navArgs()
    
    // Xử lý URI nếu là ảnh từ picker (truyền qua args dưới dạng String)
    val wallpaperUri: android.net.Uri? by lazy {
        args.wallpaper.imageUrl.takeIf { it.startsWith("content://") }?.let { android.net.Uri.parse(it) }
    }
    
    var favoriteAnim: Animation? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        timber.log.Timber.d("PermissionCheck Result: $isGranted")
        if (isGranted) {
            viewModel.downloadWallpaper()
        } else {
            displayToast("Cần cấp quyền để lưu ảnh")
        }
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        // Luôn ưu tiên hiển thị title từ args.wallpaper
        binding.tvImageName.text = args.wallpaper.title
        
        viewModel.setWallpaper(args.wallpaper)
        initView()
        settingEvent()
    }

    fun checkPermissionAndDownload() {
        timber.log.Timber.d("Check Permission Started")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            viewModel.downloadWallpaper()
        }
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.wallpaper to it.isGif }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { (wallpaper, isGif) ->
                wallpaper?.let {
                    timber.log.Timber.d("DEBUG: URL=${it.imageUrl}, isGif=$isGif")
                    if (isGif) {
                        com.bumptech.glide.Glide.with(requireContext())
                            .asGif()
                            .load(it.imageUrl)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE)
                            .into(binding.ivFullWallpaper)
                    } else {
                        // Nếu là ảnh từ Picker, dùng setImageURI
                        if (wallpaperUri != null) {
                            binding.ivFullWallpaper.setImageURI(wallpaperUri)
                        } else {
                            binding.ivFullWallpaper.loadImage(it.imageUrl)
                        }
                    }
                }
            }

        // Thêm observer cho trạng thái loading
        viewModel.uiState
            .map { it.isLoading || it.isSettingWallpaper }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoading ->
                showHideLoading(isLoading)
            }

        viewModel.uiState
            .map { it.isFavorite }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isFavorite ->
                val icon = if (isFavorite) {
                    R.drawable.ic_heart_filled
                } else {
                    R.drawable.ic_heart
                }
                binding.fabFavorite.setImageResource(icon)
                
                // Clear color filter if using ic_heart_filled which is already red
                if (isFavorite) {
                    binding.fabFavorite.clearColorFilter()
                } else {
                    binding.fabFavorite.setColorFilter(android.graphics.Color.BLACK)
                }
            }
    }

    override fun onDestroyView() {
        releaseAnimation()
        super.onDestroyView()
    }
}
