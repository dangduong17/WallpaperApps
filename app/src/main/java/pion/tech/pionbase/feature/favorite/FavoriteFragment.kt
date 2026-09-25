package pion.tech.pionbase.feature.favorite

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentFavoriteBinding
import pion.tech.pionbase.feature.home.adapter.TopWallpaperAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class FavoriteFragment :
    BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>(
        FragmentFavoriteBinding::inflate,
        FavoriteViewModel::class
    ), TopWallpaperAdapter.Listener {

    val adapter = TopWallpaperAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.favoritesUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        binding.tvEmpty.isVisible = list.isEmpty()
                        adapter.submitList(list)
                    }
                )
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToWallpaperDetailFragment(item)
        navigator.safeNavigate(action)
    }
}
