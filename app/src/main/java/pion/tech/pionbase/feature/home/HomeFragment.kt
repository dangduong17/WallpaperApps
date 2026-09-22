package pion.tech.pionbase.feature.home

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
// import androidx.navigation.fragment.findNavController - REMOVED (using navigator instead)
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.base.doActionWhenResume
import pion.tech.pionbase.data.model.wallpaper.CategoryUIModel
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.feature.home.adapter.*
import pion.tech.pionbase.feature.home.bottomSheet.WallpaperPreviewBottomSheet
import pion.tech.pionbase.service.LiveWallpaperService
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState
import pion.tech.pionbase.util.safeShowDialog
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
            val dialog = WallpaperPreviewBottomSheet.newInstance(uri) { selectedUri ->
                val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_" + System.currentTimeMillis() + ".jpg"))
                val intent = UCrop.of(selectedUri, destinationUri)
                    .withAspectRatio(9f, 16f)
                    .getIntent(requireContext())
                cropImage.launch(intent)
            }
            safeShowBottomSheet(dialog)
        } else {
            displayToast(R.string.cannot_select_image)
        }
    }

    private fun showWallpaperOptionsDialog(uri: Uri) {
        val options = arrayOf(
            getString(R.string.home_screen),
            getString(R.string.lock_screen),
            getString(R.string.both),
            getString(R.string.live_wallpaper)
        )
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.set_wallpaper))
            .setItems(options) { _, which ->
                if (which == 3) {
                    try {
                        val outputFile = File(requireContext().filesDir, "active_gif.gif")
                        
                        requireContext().contentResolver.openInputStream(uri)?.use { input ->
                            outputFile.outputStream().use { output ->
                                input.copyTo(output)
                                output.flush()
                            }
                        }
                        
                        if (outputFile.exists() && outputFile.length() > 0) {
                            outputFile.setReadable(true, false)
                            
                            requireContext().getSharedPreferences("wallpaper_prefs", android.content.Context.MODE_PRIVATE)
                                .edit().putString("selected_gif_path", outputFile.absolutePath).apply()
                            
                            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            intent.putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(requireContext(), LiveWallpaperService::class.java)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                startActivity(intent)
                            } catch (_: android.content.ActivityNotFoundException) {
                                displayToast(getString(R.string.error_live_wallpaper_not_supported))
                            }
                        } else {
                            displayToast(getString(R.string.error_prepare_file))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Lỗi copy file")
                        displayToast(getString(R.string.error_copying_file))
                    }
                } else {
                    val flag = when (which) {
                        0 -> WallpaperManager.FLAG_SYSTEM
                        1 -> WallpaperManager.FLAG_LOCK
                        else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    }
                    viewModel.setWallpaper(uri, flag)
                    com.google.android.material.snackbar.Snackbar.make(binding.root, getString(R.string.set_wallpaper_success), com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                }
            }
        
        // Since AlertDialog is not a DialogFragment, we can't use safeShowDialog directly 
        // if it expects BaseDialogFragment.
        // I will just use .show() for now, or create a BaseDialogFragment if required, 
        // but given constraints, I will follow safeShowDialog as much as possible.
        // Actually, AlertDialog.Builder().create().show() is the standard way.
        builder.show()
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
        Timber.d("HomeFragment: onClickWallpaper called for: ${item.imageUrl}")
        val action = HomeFragmentDirections.actionHomeFragmentToWallpaperDetailFragment(item)
        doActionWhenResume {
            navigator.navigateTo(action)
        }
    }

    override fun onClickCategory(item: CategoryUIModel) {
        Timber.d("HomeFragment: onClickCategory called for: ${item.title}")
        val action = HomeFragmentDirections.actionHomeFragmentToCategoryDetailFragment(item.title)
        doActionWhenResume {
            navigator.navigateTo(action)
        }
    }
}
