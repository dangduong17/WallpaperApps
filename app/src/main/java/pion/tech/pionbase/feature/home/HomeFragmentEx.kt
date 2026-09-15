package pion.tech.pionbase.feature.home

import android.view.View
import pion.tech.pionbase.R
import pion.tech.pionbase.feature.home.dialog.DemoDialog
import pion.tech.pionbase.feature.home.dialog.ExitAppDialog
import pion.tech.pionbase.util.safeShowDialog
import pion.tech.pionbase.util.setPreventDoubleClick
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HomeFragment.initView() {
    binding.apply {
        rvFeatured.adapter = featuredAdapter
        rvTopWallpaper.adapter = topWallpaperAdapter
        rvCategories.adapter = categoryAdapter
        
        tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                if (tab?.position == 0) {
                    layoutHome.visibility = View.VISIBLE
                    rvCategories.visibility = View.GONE
                } else {
                    layoutHome.visibility = View.GONE
                    rvCategories.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
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
