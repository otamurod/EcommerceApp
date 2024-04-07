package uz.otamurod.ecommerceapp.dialog

import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.databinding.ResetPasswordDialogBinding

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val binding = ResetPasswordDialogBinding.inflate(layoutInflater)
    dialog.setContentView(binding.root)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    binding.buttonSendResetPassword.setOnClickListener {
        val email = binding.editTextResetPassword.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    binding.buttonCancelResetPassword.setOnClickListener {
        dialog.dismiss()
    }
}