package pion.tech.pionbase.feature.home

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
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
import pion.tech.pionbase.util.isGif
import pion.tech.pionbase.util.isVideo
import pion.tech.pionbase.util.safeShowBottomSheet
import timber.log.Timber
import java.io.File

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class,
    ), FeaturedAdapter.Listener, TopWallpaperAdapter.Listener, CategoryAdapter.Listener {
    
    val featuredAdapter = FeaturedAdapter()
    val topWallpaperAdapter = TopWallpaperAdapter()
    val categoryAdapter = CategoryAdapter()

    private val cropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val resultUri = UCrop.getOutput(data)
                if (resultUri != null) {
                    showWallpaperOptionsDialog(resultUri)
                }
            }
        }
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        Timber.d("HomeFragment: PickVisualMedia result: $uri")
        if (uri != null) {
            val contentResolver = requireContext().contentResolver
            if (uri.isGif(contentResolver) || uri.isVideo(contentResolver)) {
                val intent = Intent(requireContext(), EditWallpaperActivity::class.java).apply {
                    putExtra(pion.tech.pionbase.util.Constant.KEY_URI, uri)
                }
                startActivity(intent)
            } else {
                val dialog = WallpaperPreviewBottomSheet.newInstance(uri) { selectedUri ->
                    val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_" + System.currentTimeMillis() + ".jpg"))
                    val intent = UCrop.of(selectedUri, destinationUri)
                        .withAspectRatio(9f, 16f)
                        .getIntent(requireContext())
                    cropImage.launch(intent)
                }
                safeShowBottomSheet(dialog)
            }
        } else {
            displayToast(R.string.cannot_select_image)
        }
    }

    private fun showWallpaperOptionsDialog(uri: Uri) {
        val options = arrayOf(
            getString(R.string.home_screen),
            getString(R.string.lock_screen),
            getString(R.string.both)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.set_wallpaper))
            .setItems(options) { _, which ->
                val flag = when (which) {
                    0 -> WallpaperManager.FLAG_SYSTEM
                    1 -> WallpaperManager.FLAG_LOCK
                    else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
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

        // Observe wallpaper events
        viewModel.uiEvent
            .collectFlowOnView(viewLifecycleOwner) { event ->
                when (event) {
                    is HomeEvent.SetWallpaperSuccess -> {
                        displayToast(getString(R.string.set_wallpaper_success))
                    }
                    is HomeEvent.SetWallpaperError -> {
                        displayToast(getString(R.string.error, event.throwable.message ?: ""))
                    }
                }
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        Timber.d("HomeFragment: onClickWallpaper called for: ${item.imageUrl}")
        val action = HomeFragmentDirections.actionHomeFragmentToWallpaperDetailFragment(item)
        navigator.safeNavigate(action)
    }

    override fun onClickCategory(item: CategoryUIModel) {
        Timber.d("HomeFragment: onClickCategory called for: ${item.title}")
        val action = HomeFragmentDirections.actionHomeFragmentToCategoryDetailFragment(item.title)
        navigator.safeNavigate(action)
    }
}
