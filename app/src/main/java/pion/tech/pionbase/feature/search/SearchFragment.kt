package pion.tech.pionbase.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.wallpaper.WallpaperUIModel
import pion.tech.pionbase.databinding.FragmentSearchBinding
import pion.tech.pionbase.feature.home.adapter.TopWallpaperAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(
        FragmentSearchBinding::inflate,
        SearchViewModel::class
    ), TopWallpaperAdapter.Listener {

    val adapter = TopWallpaperAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.searchResultUiState }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { uiState ->
                uiState.handleUiState(
                    onSuccess = { list ->
                        binding.tvNoResult.isVisible = list.isEmpty() && binding.edtSearch.text.isNotEmpty()
                        adapter.submitList(list)
                    },
                    onNone = {
                        adapter.submitList(emptyList())
                        binding.tvNoResult.isVisible = false
                    }
                )
            }
    }

    override fun onClickWallpaper(item: WallpaperUIModel) {
        val action = SearchFragmentDirections.actionSearchFragmentToWallpaperDetailFragment(item)
        navigator.navigateTo(action)
    }
}
