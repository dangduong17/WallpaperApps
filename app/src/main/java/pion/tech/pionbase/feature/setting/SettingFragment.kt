package pion.tech.pionbase.feature.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
import pion.tech.pionbase.feature.home.EditWallpaperActivity
import pion.tech.pionbase.util.collectFlowOnView

class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class,
    ) {

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val intent = Intent(requireContext(), EditWallpaperActivity::class.java).apply {
                putExtra("uri", uri)
            }
            startActivity(intent)
        }
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        backEvent()
        bindView()
        languageEvent()
        autoWallpaperEvent()
        photoPickerEvent()
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
            .map { it.autoWallpaperEnabled }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { isEnabled ->
                if (binding.swAutoChange.isChecked != isEnabled) {
                    binding.swAutoChange.isChecked = isEnabled
                }
            }
    }
}
