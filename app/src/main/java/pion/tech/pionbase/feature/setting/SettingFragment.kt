package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

import android.content.Intent
import pion.tech.pionbase.feature.home.EditWallpaperActivity

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
        dataStoreRepository.getLanguage().collectFlowOnView(viewLifecycleOwner) { result ->
            if (result is pion.tech.pionbase.util.Result.Success) {
                val localeCode = result.data
                val languageName = when(localeCode) {
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
