package pion.tech.pionbase.feature.splash

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.app.MainActivity
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSplashBinding
import pion.tech.pionbase.util.collectFlowOnView

class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(
        FragmentSplashBinding::inflate,
        SplashViewModel::class,
    ) {
    var progressAnimator: ValueAnimator? = null

    override fun init(view: View, savedInstanceState: Bundle?) {
        initView()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        // Observe first launch state to navigate if animation finished but data was slow
        viewModel.uiState
            .map { it.isFirstLaunch }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isFirstLaunch ->
                if (isFirstLaunch != null && binding.progressBar.progress == 100) {
                    goToNextScreen()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseAnimation()
    }
}
