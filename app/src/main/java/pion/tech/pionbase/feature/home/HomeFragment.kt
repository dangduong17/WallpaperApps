package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.CategoryAdapter
import pion.tech.pionbase.feature.home.adapter.FeaturedAdapter
import pion.tech.pionbase.feature.home.adapter.TopWallpaperAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ) {
    
    val featuredAdapter = FeaturedAdapter()
    val topWallpaperAdapter = TopWallpaperAdapter()
    val categoryAdapter = CategoryAdapter()

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
    }
}
