package pion.tech.pionbase.feature.home

import android.view.View
import com.google.android.material.tabs.TabLayout
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.dialog.ExitAppDialog
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

private const val TAB_HOME = 0
private const val TAB_CATEGORIES = 1

fun HomeFragment.initView() {
    binding.apply {
        featuredAdapter.setListener(this@initView)
        topWallpaperAdapter.setListener(this@initView)
        
        rvFeatured.adapter = featuredAdapter
        rvTopWallpaper.adapter = topWallpaperAdapter
        rvCategories.adapter = categoryAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
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
    binding.ivProfile.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_homeFragment_to_settingFragment)
    }
}

fun HomeFragment.showDemoDialogEvent() {
    // binding.btnShowDialog.setPreventDoubleClick {
    //    val dialog = DemoDialog.newInstance("Demo Dialog")
    //    dialog.setListener(this)
    //    safeShowDialog(dialog)
    // }
}
