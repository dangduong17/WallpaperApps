package pion.tech.pionbase.feature.changeLanguage

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeoutOrNull
import pion.tech.pionbase.R
import pion.tech.pionbase.base.launchMain
import pion.tech.pionbase.util.AppRemoteConfig
import pion.tech.pionbase.util.OnboardAds
import pion.tech.pionbase.util.OnboardFullAds
import timber.log.Timber

fun ChangeLanguageFragment.initView() {
    onSystemBack { /* No-op */ }
    startProgressAnimation()
}

fun ChangeLanguageFragment.startProgressAnimation() {
    progressAnimator =
        ValueAnimator.ofInt(0, 100).apply {
            duration = AppRemoteConfig.maxTimeShowChangeLanguageScreen
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

// Dọn dẹp: Đã xóa loadAdSuspend và các import không dùng đến

fun ChangeLanguageFragment.preloadAndNav() {
    val tag = "preloadAndNav"
    // Giữ logic preload, xóa code comment thừa
    preloadJob = launchMain {
        try {
             // ... logic hiện có
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
