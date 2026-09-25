package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
import pion.tech.pionbase.util.ThemeManager
import pion.tech.pionbase.util.collectFlowOnView

class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class,
    ) {

    override fun init(view: View, savedInstanceState: Bundle?) {
        backEvent()
        bindView()
        historyEvent()
        languageEvent()
        themeEvent()
        dynamicColorEvent()
        autoWallpaperEvent()
        batterySaverEvent()
        cacheEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.currentLanguageCode }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { localeCode ->
                if (localeCode.isNotEmpty()) {
                    val languageName = when (localeCode) {
                        "vi" -> getString(R.string.vietnamese)
                        else -> getString(R.string.english_lang)
                    }
                    binding.tvLanguageName.text = languageName
                }
            }

        viewModel.uiState
            .map { it.themeMode }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { mode ->
                val themeText = when (mode) {
                    ThemeManager.MODE_LIGHT -> getString(R.string.theme_light)
                    ThemeManager.MODE_DARK -> getString(R.string.theme_dark)
                    else -> getString(R.string.theme_system)
                }
                binding.tvThemeValue.text = themeText
            }

        viewModel.uiState
            .map { it.dynamicColorEnabled }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isEnabled ->
                if (binding.swDynamicColor.isChecked != isEnabled) {
                    binding.swDynamicColor.isChecked = isEnabled
                }
            }

        viewModel.uiState
            .map { it.isDynamicColorAvailable }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isAvailable ->
                binding.btnDynamicColor.isVisible = isAvailable
            }

        viewModel.uiState
            .map { it.autoWallpaperEnabled }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isEnabled ->
                if (binding.swAutoChange.isChecked != isEnabled) {
                    binding.swAutoChange.isChecked = isEnabled
                }
            }

        viewModel.uiState
            .map { it.batterySaverEnabled }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isEnabled ->
                if (binding.swBatterySaver.isChecked != isEnabled) {
                    binding.swBatterySaver.isChecked = isEnabled
                }
            }

        viewModel.uiState
            .map { it.cacheSizeFormatted }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { formattedSize ->
                binding.tvCacheSize.text = formattedSize
            }

        viewModel.uiState
            .map { it.isClearingCache }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isClearing ->
                showHideLoading(isClearing)
            }
    }
}
