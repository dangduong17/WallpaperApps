package pion.tech.pionbase.feature.wallpaperDetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentWallpaperDetailBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.loadImage

class WallpaperDetailFragment : BaseFragment<FragmentWallpaperDetailBinding, WallpaperDetailViewModel>(
    FragmentWallpaperDetailBinding::inflate,
    WallpaperDetailViewModel::class
) {
    private val args: WallpaperDetailFragmentArgs by navArgs()

    override fun init(view: View, savedInstanceState: Bundle?) {
        viewModel.setWallpaper(args.wallpaper)
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.wallpaper }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { wallpaper ->
                wallpaper?.let {
                    binding.ivFullWallpaper.loadImage(it.imageUrl)
                }
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
}
