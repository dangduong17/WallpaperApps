package pion.tech.pionbase.feature.home

import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.tabs.TabLayout
import androidx.navigation.fragment.findNavController
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.dialog.ExitAppDialog
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

internal const val TAB_HOME = 0
internal const val TAB_CATEGORIES = 1

fun HomeFragment.initView() {
    binding.apply {
        rvFeatured.adapter = null
        rvTopWallpaper.adapter = null
        rvCategories.adapter = null
        
        featuredAdapter.setListener(this@initView)
        topWallpaperAdapter.setListener(this@initView)
        categoryAdapter.setListener(this@initView)
        
        rvFeatured.adapter = featuredAdapter
        rvTopWallpaper.adapter = topWallpaperAdapter
        rvCategories.adapter = categoryAdapter
        
        timber.log.Timber.d("HomeFragment: FeaturedAdapter listener set to: ${this@initView}")

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    viewModel.setSelectedTab(position)
                    when (position) {
                        TAB_HOME -> {
                            layoutHome.visibility = View.VISIBLE
                            rvCategories.visibility = View.GONE
                        }
                        TAB_CATEGORIES -> {
                            layoutHome.visibility = View.GONE
                            rvCategories.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}

fun HomeFragment.onBackEvent() {
    onSystemBack {
        backEvent()
    }
}

fun HomeFragment.backEvent() {
    val dialog = ExitAppDialog()
    dialog.show(childFragmentManager)
}

fun HomeFragment.settingEvent() {
    // Set listeners on the containers (FrameLayouts) for a larger touch target
    binding.btnProfile.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
    }

    binding.btnFavorite.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_favoriteFragment)
    }

    binding.btnSearch.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }
    
    binding.btnHome.setOnClickListener {
        binding.tabLayout.getTabAt(TAB_HOME)?.select()
    }

    binding.btnPickPhoto.setPreventDoubleClickScaleView {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

fun HomeFragment.showDemoDialogEvent() {
    // Implementation for demo dialog if needed
}
