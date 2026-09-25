package pion.tech.pionbase.feature.history

import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayout
import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun HistoryFragment.initView() {
    adapter.setListener(this)
    binding.rvHistory.adapter = adapter

    val tabRecent = binding.tabLayout.newTab().setText(R.string.recent_views)
    val tabSetDownload = binding.tabLayout.newTab().setText(R.string.download_and_set_history)
    binding.tabLayout.addTab(tabRecent)
    binding.tabLayout.addTab(tabSetDownload)

    binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                viewModel.selectTab(it.position)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}

fun HistoryFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.safeNavigateUp()
    }

    binding.btnClear.setPreventDoubleClickScaleView {
        showClearConfirmDialog()
    }

    onSystemBack {
        navigator.safeNavigateUp()
    }
}

fun HistoryFragment.showClearConfirmDialog() {
    AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.clear_history_title))
        .setMessage(getString(R.string.clear_history_message))
        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            viewModel.clearHistory()
        }
        .setNegativeButton(getString(R.string.cancel), null)
        .show()
}
