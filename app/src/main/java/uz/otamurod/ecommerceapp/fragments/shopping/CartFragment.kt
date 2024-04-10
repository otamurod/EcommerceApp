package uz.otamurod.ecommerceapp.fragments.shopping

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.adapters.CartProductsAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentCartBinding
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon.QuantityChanging.DECREASE
import uz.otamurod.ecommerceapp.firebase.FirebaseCommon.QuantityChanging.INCREASE
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.util.VerticalItemDecoration
import uz.otamurod.ecommerceapp.viewmodel.CartViewModel

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val viewModel by viewModels<CartViewModel>()
    private val cartProductsAdapter by lazy { CartProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartProductsRV()

        lifecycleScope.launch {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.isVisible = true
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.isVisible = false
                        if (it.data!!.isEmpty()) {
                            hideOtherViews()
                            showEmptyBox()
                        } else {
                            hideEmptyBox()
                            showOtherViews()
                            cartProductsAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.isVisible = false
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    binding.tvTotalPrice.text = String.format("$ %.2f", price)
                }

            }
        }

        lifecycleScope.launch {
            viewModel.deleteProductDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete the item from the Cart")
                        .setMessage("Do you want to remove this item from your Cart?")
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }.setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteCartProduct(it)
                            dialog.dismiss()
                        }
                }

                alertDialog.create()
                alertDialog.show()
            }
        }

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.isVisible = true
            totalBoxContainer.isVisible = true
            buttonCheckout.isVisible = true
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.isVisible = false
            totalBoxContainer.isVisible = false
            buttonCheckout.isVisible = false
        }
    }

    private fun hideEmptyBox() {
        binding.layoutCarEmpty.isVisible = false
    }

    private fun showEmptyBox() {
        binding.layoutCarEmpty.isVisible = true
    }

    private fun setUpCartProductsRV() {
        binding.rvCart.apply {
            adapter = cartProductsAdapter
            addItemDecoration(VerticalItemDecoration())
        }

        cartProductsAdapter.onProductClick = {
            val bundle = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        cartProductsAdapter.onPlusClick = {
            viewModel.changeQuantity(it, INCREASE)
        }

        cartProductsAdapter.onMinusClick = {
            viewModel.changeQuantity(it, DECREASE)
        }
    }

    companion object {
        private const val TAG = "CartFragment"
    }
}