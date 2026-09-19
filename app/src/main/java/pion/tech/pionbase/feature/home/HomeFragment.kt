package pion.tech.pionbase.feature.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.*
import pion.tech.pionbase.feature.home.bottomSheet.WallpaperPreviewBottomSheet
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
            val dialog = WallpaperPreviewBottomSheet(uri) { selectedUri ->
                showWallpaperOptionsDialog(selectedUri)
            }
            dialog.show(childFragmentManager, "WallpaperPreview")
        }
    }

    private fun showWallpaperOptionsDialog(uri: android.net.Uri) {
        val options = arrayOf("Màn hình chính", "Màn hình khóa", "Cả hai")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Đặt làm hình nền")
            .setItems(options) { _, which ->
                val flag = when (which) {
                    0 -> android.app.WallpaperManager.FLAG_SYSTEM
                    1 -> android.app.WallpaperManager.FLAG_LOCK
                    else -> android.app.WallpaperManager.FLAG_SYSTEM or android.app.WallpaperManager.FLAG_LOCK
                }
                viewModel.setWallpaper(uri, flag)
            }
            .show()
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

        // Observe loading state for wallpaper
        viewModel.uiState
            .map { it.isSettingWallpaper }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isSetting ->
                showHideLoading(isSetting)
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        timber.log.Timber.d("HomeFragment: onClickWallpaper called for: ${item.imageUrl}")
        val action = HomeFragmentDirections.actionHomeFragmentToWallpaperDetailFragment(item)
        findNavController().navigate(action)
    }

    override fun onClickCategory(item: CategoryUIModel) {
        timber.log.Timber.d("HomeFragment: onClickCategory called for: ${item.title}")
        val action = HomeFragmentDirections.actionHomeFragmentToCategoryDetailFragment(item.title)
        findNavController().navigate(action)
    }
}
