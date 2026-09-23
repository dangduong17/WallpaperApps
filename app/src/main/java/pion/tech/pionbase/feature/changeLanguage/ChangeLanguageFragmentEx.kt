package pion.tech.pionbase.feature.changeLanguage

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.delay
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchMain
import timber.log.Timber

private const val CHANGE_LANGUAGE_ANIM_DURATION = 1300L

fun ChangeLanguageFragment.initView() {
    onSystemBack { /* No-op */ }
    startProgressAnimation()
}

fun ChangeLanguageFragment.startProgressAnimation() {
    progressAnimator =
        ValueAnimator.ofInt(0, 100).apply {
            duration = CHANGE_LANGUAGE_ANIM_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                runCatching {
                    val progress = animator.animatedValue as Int
                    binding.progressBar.progress = progress
                }
            }
            start()
        }
}

fun ChangeLanguageFragment.preloadAndNav() {
    val tag = "preloadAndNav"
    viewModel.preloadDataAndImages()
    preloadJob = launchMain {
        try {
            delay(CHANGE_LANGUAGE_ANIM_DURATION)
            goToNextScreen()
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "exception occurred")
            goToNextScreen()
        }
    }
}

fun ChangeLanguageFragment.goToNextScreen() {
    navigator.navigateTo(R.id.action_changeLanguageFragment_to_homeFragment)
}

fun ChangeLanguageFragment.releaseAnimation() {
    progressAnimator?.cancel()
    progressAnimator = null
}
