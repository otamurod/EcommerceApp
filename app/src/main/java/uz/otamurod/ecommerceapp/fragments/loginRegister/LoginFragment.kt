package uz.otamurod.ecommerceapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.activities.ShoppingActivity
import uz.otamurod.ecommerceapp.databinding.FragmentLoginBinding
import uz.otamurod.ecommerceapp.dialog.setupBottomSheetDialog
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonLogin.setOnClickListener {
                val email = editTextEmailLogin.text.toString().trim()
                val password = editTextPasswordLogin.text.toString()

                viewModel.login(email, password)
            }

            tvForgotPasswordLogin.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonLogin.revertAnimation()

                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLogin.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.reset_link_sent),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}