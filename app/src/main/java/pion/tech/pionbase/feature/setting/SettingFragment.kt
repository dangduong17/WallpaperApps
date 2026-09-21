package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.displayToast

class SettingFragment :
    BaseFragment<FragmentSettingBinding, SettingViewModel>(
        FragmentSettingBinding::inflate,
        SettingViewModel::class,
    ) {

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            displayToast("Selected media: $uri")
            // Here you can save this URI or navigate to detail to set as wallpaper
        }
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        backEvent()
        bindView()
        languageEvent()
        developerEvent()
        advertisementEvent()
        policyEvent()
        resetIapEvent()
        gdprEvent()
        resetGDPR()
        photoPickerEvent()
    }

    override fun subscribeObserver(view: View) {
        // Cập nhật tên ngôn ngữ dựa trên lựa chọn hiện tại
        dataStoreRepository.getLanguage().collectFlowOnView(viewLifecycleOwner) { result ->
            if (result is pion.tech.pionbase.util.Result.Success) {
                val localeCode = result.data
                // Map localeCode to display name. Simple approach:
                val languageName = when(localeCode) {
                    "vi" -> getString(R.string.vietnamese)
                    else -> getString(R.string.english_lang)
                }
                binding.tvLanguageName.text = languageName
            }
        }
    }
}
