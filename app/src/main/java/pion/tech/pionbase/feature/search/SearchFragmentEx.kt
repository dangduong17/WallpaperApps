package pion.tech.pionbase.feature.search

import androidx.core.widget.addTextChangedListener
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun SearchFragment.initView() {
    adapter.setListener(this)
    binding.rvSearchResult.adapter = adapter
    
    binding.edtSearch.addTextChangedListener { text ->
        viewModel.search(text.toString())
    }
}

fun SearchFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.navigateUp()
    }
}
