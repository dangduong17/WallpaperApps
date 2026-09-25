package pion.tech.pionbase.feature.urlWallpaper

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentUrlWallpaperBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

class UrlWallpaperFragment : BaseFragment<FragmentUrlWallpaperBinding, UrlWallpaperViewModel>(
    FragmentUrlWallpaperBinding::inflate,
    UrlWallpaperViewModel::class,
) {
    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.isLoading }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

        viewModel.uiEvent
            .collectFlowOnView(viewLifecycleOwner) { event ->
                when (event) {
                    is UrlWallpaperEvent.SetWallpaperSuccess -> {
                        displayToast(getString(R.string.set_wallpaper_success))
                    }
                    is UrlWallpaperEvent.SetWallpaperError -> {
                        displayToast(getString(R.string.error, event.throwable.message ?: ""))
                    }
                }
            }
    }
}
