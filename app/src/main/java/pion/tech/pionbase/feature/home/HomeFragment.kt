package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.*
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ), FeaturedAdapter.Listener, TopWallpaperAdapter.Listener, CategoryAdapter.Listener {
    
    val featuredAdapter = FeaturedAdapter()
    val topWallpaperAdapter = TopWallpaperAdapter()
    val categoryAdapter = CategoryAdapter()

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            displayToast("Selected photo: $uri")
            val customWallpaper = WallpaperUIModel("Custom", uri.toString())
            val action = HomeFragmentDirections.actionHomeFragmentToWallpaperDetailFragment(customWallpaper)
            navigator.navigateTo(action)
        }
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        // Observe featured wallpapers
        viewModel.uiState
            .map { it.featuredUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        featuredAdapter.submitList(list)
                    }
                )
            }

        // Observe top wallpapers
        viewModel.uiState
            .map { it.topWallpaperUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        topWallpaperAdapter.submitList(list)
                    }
                )
            }

        // Observe categories
        viewModel.uiState
            .map { it.categoriesUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        categoryAdapter.submitList(list)
                    }
                )
            }

        // Observe selected tab
        viewModel.uiState
            .map { it.selectedTab }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { selectedTab ->
                binding.tabLayout.getTabAt(selectedTab)?.select()
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToWallpaperDetailFragment(item)
        navigator.navigateTo(action)
    }

    override fun onClickCategory(item: CategoryUIModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToCategoryDetailFragment(item.title)
        navigator.navigateTo(action)
    }
}
