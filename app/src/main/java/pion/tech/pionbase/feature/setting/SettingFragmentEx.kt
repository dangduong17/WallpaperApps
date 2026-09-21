package pion.tech.pionbase.feature.setting

import android.annotation.SuppressLint
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView
import androidx.appcompat.app.AlertDialog
import android.widget.NumberPicker
import androidx.work.*
import java.util.concurrent.TimeUnit
import pion.tech.pionbase.worker.AutoWallpaperWorker

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

fun SettingFragment.autoWallpaperEvent() {
    binding.swAutoChange.setPreventDoubleClickScaleView {
        val isCurrentlyEnabled = viewModel.uiState.value.autoWallpaperEnabled
        if (!isCurrentlyEnabled) {
            showIntervalDialog()
        } else {
            viewModel.setAutoWallpaperEnabled(false)
            WorkManager.getInstance(requireContext()).cancelUniqueWork(AutoWallpaperWorker.WORK_NAME)
        }
    }
}

fun SettingFragment.showIntervalDialog() {
    val picker = NumberPicker(requireContext()).apply {
        minValue = AutoWallpaperWorker.MIN_INTERVAL
        maxValue = AutoWallpaperWorker.MAX_INTERVAL
        value = viewModel.uiState.value.autoWallpaperInterval.toInt().coerceIn(
            AutoWallpaperWorker.MIN_INTERVAL, 
            AutoWallpaperWorker.MAX_INTERVAL
        )
    }
    AlertDialog.Builder(requireContext())
        .setTitle("Chọn khoảng thời gian (phút)")
        .setView(picker)
        .setPositiveButton("OK") { _, _ ->
            val interval = picker.value.toLong()
            viewModel.setAutoWallpaperEnabled(true)
            viewModel.setAutoWallpaperInterval(interval)
            scheduleAutoWallpaper(interval)
        }
        .setNegativeButton("Cancel", null)
        .show()
}

fun SettingFragment.scheduleAutoWallpaper(intervalMinutes: Long) {
    val workRequest = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(intervalMinutes, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
        AutoWallpaperWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

fun SettingFragment.photoPickerEvent() {
    binding.btnPickPhoto.setPreventDoubleClickScaleView {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
