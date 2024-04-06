package uz.otamurod.ecommerceapp.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.data.User
import uz.otamurod.ecommerceapp.databinding.FragmentRegisterBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.RegisterViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegister.setOnClickListener {
                val user = User(
                    editTextFirstNameRegister.text.toString().trim(),
                    editTextLastNameRegister.text.toString().trim(),
                    editTextEmailRegister.text.toString().trim()
                )

                val password = editTextPasswordRegister.text.toString()

                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d(TAG, "onViewCreated: ${it.data.toString()}")
                        binding.buttonRegister.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "onViewCreated: ${it.message.toString()}")
                        binding.buttonRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}