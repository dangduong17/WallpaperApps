package pion.tech.pionbase.feature.setting

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.net.toUri
import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun SettingFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
    // New design uses ivMenu or similar, adding back functionality to ivMenu for now
    binding.ivMenu.setPreventDoubleClickScaleView {
        onBackPressed()
    }
}

fun SettingFragment.onBackPressed() {
    navigator.navigateUp()
}

@SuppressLint("SetTextI18n")
fun SettingFragment.bindView() {
    // Version display logic removed from new design layout but kept here for reference
}

fun SettingFragment.languageEvent() {
    binding.btnLanguage.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_settingFragment_to_languageFragment)
    }
}

fun SettingFragment.developerEvent() {
    // binding.btnDeveloper.setPreventDoubleClickScaleView {
    //    DeveloperDialog().show(childFragmentManager)
    // }
}

fun SettingFragment.advertisementEvent() {
    // binding.btnAdvertisement.setPreventDoubleClickScaleView {
    //    AdvertisementDialog().show(childFragmentManager)
    // }
}

fun SettingFragment.policyEvent() {
    // binding.btnPolicy.setPreventDoubleClickScaleView {
    //    runCatching {
    //        val browserIntent =
    //            Intent(
    //                Intent.ACTION_VIEW,
    //                "https://sites.google.com/piontech.co/voicelockscreen".toUri(),
    //            )
    //        startActivity(browserIntent)
    //    }
    // }
}

fun SettingFragment.resetIapEvent() {
    // binding.btnResetIap.isVisible = BuildConfig.DEBUG
}

fun SettingFragment.gdprEvent() {
    // binding.btnGdpr.setPreventDoubleClickScaleView { }
}

fun SettingFragment.resetGDPR() {
    // if (BuildConfig.DEBUG) {
    //    binding.btnResetGdpr.isVisible = true
    // }
}
