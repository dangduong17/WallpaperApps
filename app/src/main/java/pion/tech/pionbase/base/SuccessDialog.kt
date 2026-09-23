package pion.tech.pionbase.base

import android.os.Bundle
import pion.tech.pionbase.databinding.DialogSuccessBinding
import pion.tech.pionbase.util.safeDelay

class SuccessDialog(private val message: String? = null) : BaseDialogFragment<DialogSuccessBinding>(DialogSuccessBinding::inflate) {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        message?.let {
            binding.tvMessage.text = it
        }
        safeDelay(1500) {
            if (isAdded) {
                dismiss()
            }
        }
    }
}
