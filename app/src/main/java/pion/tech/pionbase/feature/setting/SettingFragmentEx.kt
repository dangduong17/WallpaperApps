package pion.tech.pionbase.feature.setting

import android.annotation.SuppressLint
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import pion.tech.pionbase.R
import pion.tech.pionbase.util.ThemeManager
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import pion.tech.pionbase.worker.AutoWallpaperWorker
import timber.log.Timber

fun SettingFragment.backEvent() {
    onSystemBack {
        onBackPressed()
    }
    binding.ivMenu.setPreventDoubleClickScaleView {
        onBackPressed()
    }
}

fun SettingFragment.onBackPressed() {
    navigator.navigateUp()
}

@SuppressLint("SetTextI18n")
fun SettingFragment.bindView() {
}

fun SettingFragment.languageEvent() {
    binding.btnLanguage.setPreventDoubleClickScaleView {
        navigator.navigateTo(R.id.action_settingFragment_to_languageFragment)
    }
}

fun SettingFragment.themeEvent() {
    binding.btnTheme.setPreventDoubleClickScaleView {
        showThemeDialog()
    }
}

fun SettingFragment.showThemeDialog() {
    val currentTheme = viewModel.uiState.value.themeMode
    val options = arrayOf(
        getString(R.string.theme_system),
        getString(R.string.theme_light),
        getString(R.string.theme_dark),
    )

    AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.choose_theme))
        .setSingleChoiceItems(options, currentTheme) { dialog, which ->
            if (which != currentTheme) {
                viewModel.setThemeMode(which)
                ThemeManager.applyThemeMode(which)
            }
            dialog.dismiss()
        }
        .setNegativeButton(getString(R.string.cancel), null)
        .show()
}

fun SettingFragment.dynamicColorEvent() {
    binding.swDynamicColor.setPreventDoubleClickScaleView {
        val isCurrentlyEnabled = viewModel.uiState.value.dynamicColorEnabled
        val newEnabled = !isCurrentlyEnabled
        viewModel.setDynamicColorEnabled(newEnabled)
        ThemeManager.applyDynamicColorsIfEnabled(requireActivity().application, newEnabled)
        requireActivity().recreate()
    }
}

fun SettingFragment.autoWallpaperEvent() {
    binding.swAutoChange.setPreventDoubleClickScaleView {
        val isCurrentlyEnabled = viewModel.uiState.value.autoWallpaperEnabled
        if (!isCurrentlyEnabled) {
            showIntervalDialog()
        } else {
            viewModel.setAutoWallpaperEnabled(false)
            Timber.d("AutoWallpaper: Attempting to cancel work")
            WorkManager.getInstance(requireContext()).cancelUniqueWork(AutoWallpaperWorker.WORK_NAME)
        }
    }
}

fun SettingFragment.batterySaverEvent() {
    binding.swBatterySaver.setPreventDoubleClickScaleView {
        val isCurrentlyEnabled = viewModel.uiState.value.batterySaverEnabled
        val newEnabled = !isCurrentlyEnabled
        viewModel.setBatterySaverEnabled(newEnabled)
    }
}

fun SettingFragment.showIntervalDialog() {
    val picker = NumberPicker(requireContext()).apply {
        minValue = AutoWallpaperWorker.MIN_INTERVAL
        maxValue = AutoWallpaperWorker.MAX_INTERVAL
        value = viewModel.uiState.value.autoWallpaperInterval.toInt().coerceIn(
            AutoWallpaperWorker.MIN_INTERVAL, 
            AutoWallpaperWorker.MAX_INTERVAL,
        )
    }
    AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.title_interval_dialog))
        .setView(picker)
        .setPositiveButton(getString(R.string.ok)) { _, _ ->
            val interval = picker.value.toLong()
            viewModel.setAutoWallpaperEnabled(true)
            viewModel.setAutoWallpaperInterval(interval)
            scheduleAutoWallpaper(interval)
        }
        .setNegativeButton(getString(R.string.cancel), null)
        .show()
}

fun SettingFragment.scheduleAutoWallpaper(intervalMinutes: Long) {
    val workRequest = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(intervalMinutes, TimeUnit.MINUTES)
        .build()
    WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
        AutoWallpaperWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest,
    )
}
