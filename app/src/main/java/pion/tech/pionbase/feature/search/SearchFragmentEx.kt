package pion.tech.pionbase.feature.search

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import pion.tech.pionbase.feature.search.adapter.SuggestionAdapter
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun SearchFragment.initView() {
    adapter.setListener(this)
    binding.rvSearchResult.adapter = adapter
    
    suggestionAdapter.setListener(object : SuggestionAdapter.Listener {
        override fun onClickSuggestion(suggestion: String) {
            binding.edtSearch.setText(suggestion)
            viewModel.searchNow(suggestion)
            binding.rvSuggestions.visibility = View.GONE
        }
    })
    binding.rvSuggestions.adapter = suggestionAdapter
    
    binding.edtSearch.addTextChangedListener { text ->
        viewModel.onQueryChanged(text?.toString() ?: "")
    }

    binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            viewModel.searchNow(v.text.toString())
            true
        } else {
            false
        }
    }
}

fun SearchFragment.settingEvent() {
    binding.ivBack.setPreventDoubleClickScaleView {
        navigator.navigateUp()
    }
}
