package pion.tech.pionbase.feature.wallpaperDetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
    }
}
