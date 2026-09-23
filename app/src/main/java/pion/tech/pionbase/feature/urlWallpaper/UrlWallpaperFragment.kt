package pion.tech.pionbase.feature.urlWallpaper

import android.os.Bundle
import android.view.View
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentUrlWallpaperBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

class UrlWallpaperFragment : BaseFragment<FragmentUrlWallpaperBinding, UrlWallpaperViewModel>(
    FragmentUrlWallpaperBinding::inflate,
    UrlWallpaperViewModel::class
) {
    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState.collectFlowOnView(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            if (state.isSuccess) {
                displayToast(getString(R.string.wallpaper_set_success))
            }
            state.error?.let {
                displayToast(getString(R.string.error, it.message))
            }
        }
    }
}
