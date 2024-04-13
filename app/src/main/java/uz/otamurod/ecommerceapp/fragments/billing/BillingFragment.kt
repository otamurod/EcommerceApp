package uz.otamurod.ecommerceapp.fragments.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.adapters.AddressAdapter
import uz.otamurod.ecommerceapp.adapters.BillingProductsAdapter
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.data.CartProduct
import uz.otamurod.ecommerceapp.data.order.Order
import uz.otamurod.ecommerceapp.data.order.OrderStatus
import uz.otamurod.ecommerceapp.databinding.FragmentBillingBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.BillingViewModel
import uz.otamurod.ecommerceapp.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        products = args.cartProducts.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProductsRv()
        setUpAddressRv()
        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.isVisible = false
                totalBoxContainer.isVisible = false
                middleLine.isVisible = false
                bottomLine.isVisible = false
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select an address!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                showOrderConfirmationDialog()
            }
        }

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvTotalPrice.text = String.format("$ %.2f", totalPrice)

        lifecycleScope.launch {
            billingViewModel.addresses.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.isVisible = true
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.isVisible = false
                        addressAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.isVisible = false
                        Toast.makeText(
                            requireContext(), "Error ${it.message.toString()}", Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order was placed!", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(
                            requireContext(), "Error ${it.message.toString()}", Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items").setMessage("Do you want to order your cart items?")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("Yes") { dialog, _ ->
                    val order = Order(
                        OrderStatus.Ordered.status,
                        totalPrice,
                        products,
                        selectedAddress!!,
                        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
                        Random.nextLong(0, 100_000_000_000) + totalPrice.toLong()
                    )
                    orderViewModel.placeOrder(order)

                    dialog.dismiss()
                }
        }

        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressRv() {
        binding.rvAddress.adapter = addressAdapter

        addressAdapter.onClick = {
            selectedAddress = it
            if (!args.payment) {
                val bundle = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, bundle)
            }
        }
    }

    private fun setUpBillingProductsRv() {
        binding.rvProducts.adapter = billingProductsAdapter
        billingProductsAdapter.differ.submitList(products)
    }

    companion object {
        private const val TAG = "BillingFragment"
    }
}