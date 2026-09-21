package pion.tech.pionbase.feature.categoryDetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentCategoryDetailBinding
import pion.tech.pionbase.feature.home.adapter.TopWallpaperAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class CategoryDetailFragment :
    BaseFragment<FragmentCategoryDetailBinding, CategoryDetailViewModel>(
        FragmentCategoryDetailBinding::inflate,
        CategoryDetailViewModel::class
    ), TopWallpaperAdapter.Listener {

    internal val args: CategoryDetailFragmentArgs by navArgs()
    val adapter = TopWallpaperAdapter()

    // onViewCreated removed: logic moved to init()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
        viewModel.getWallpapersByCategory(args.categoryName)
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.wallpapersUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onLoading = {
                        showHideLoading(true)
                    },
                    onSuccess = { list ->
                        showHideLoading(false)
                        adapter.submitList(list)
                        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    },
                    onError = {
                        showHideLoading(false)
                    }
                )
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        val action = CategoryDetailFragmentDirections.actionCategoryDetailFragmentToWallpaperDetailFragment(item)
        navigator.navigateTo(action)
    }
}
