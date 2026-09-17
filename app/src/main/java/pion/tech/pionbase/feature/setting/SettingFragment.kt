package pion.tech.pionbase.feature.setting

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import pion.tech.pionbase.base.BaseFragment
import pion.tech.pionbase.databinding.FragmentSettingBinding
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
    }
}
