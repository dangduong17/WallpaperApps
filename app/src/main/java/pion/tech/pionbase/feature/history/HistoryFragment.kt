package pion.tech.pionbase.feature.history

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.data.model.history.WallpaperHistoryUIModel
import pion.tech.pionbase.databinding.FragmentHistoryBinding
import pion.tech.pionbase.feature.history.adapter.HistoryAdapter
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState

class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, HistoryViewModel>(
        FragmentHistoryBinding::inflate,
        HistoryViewModel::class
    ), HistoryAdapter.Listener {

    val adapter = HistoryAdapter()

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        settingEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.selectedTab }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { tabIndex ->
                if (binding.tabLayout.selectedTabPosition != tabIndex) {
                    binding.tabLayout.getTabAt(tabIndex)?.select()
                }
            }

        viewModel.uiState
            .map { Triple(it.selectedTab, it.recentViewsState, it.downloadAndSetHistoryState) }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { (selectedTab, recentViewsState, downloadAndSetState) ->
                val activeState = if (selectedTab == 0) recentViewsState else downloadAndSetState
                activeState.handleUiState(
                    onLoading = {
                        showHideLoading(true)
                    },
                    onSuccess = { list ->
                        showHideLoading(false)
                        binding.tvEmpty.isVisible = list.isEmpty()
                        adapter.submitList(list)
                    },
                    onError = {
                        showHideLoading(false)
                        binding.tvEmpty.isVisible = adapter.currentList.isEmpty()
                    }
                )
            }

        viewModel.uiState
            .map { it.isClearing }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isClearing ->
                showHideLoading(isClearing)
            }
    }

    override fun onClickHistoryItem(item: WallpaperHistoryUIModel) {
        val action = HistoryFragmentDirections.actionHistoryFragmentToWallpaperDetailFragment(
            item.toWallpaperUIModel()
        )
        navigator.safeNavigate(action)
    }
}
