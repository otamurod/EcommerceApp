package uz.otamurod.ecommerceapp.fragments.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.databinding.FragmentAddressBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.AddressViewModel

@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    private val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argsAddress = args.address
        if (argsAddress == null) {
            binding.buttonDelelte.visibility = View.GONE
        } else {
            binding.apply {
                edAddressTitle.setText(argsAddress.addressTitle)
                edFullName.setText(argsAddress.fullName)
                edStreet.setText(argsAddress.street)
                edPhone.setText(argsAddress.phoneNumber)
                edCity.setText(argsAddress.city)
                edState.setText(argsAddress.state)
                buttonSave.text = getString(R.string.update)
            }
        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle, fullName, street, phone, city, state)

                if (argsAddress == null) {
                    viewModel.addAddress(address)
                } else {
                    viewModel.updateAddress(address, argsAddress)
                }
            }

            buttonDelelte.setOnClickListener {
                viewModel.removeAddress(argsAddress!!)
            }

            imageAddressClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}