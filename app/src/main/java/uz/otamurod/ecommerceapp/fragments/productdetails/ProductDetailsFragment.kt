package uz.otamurod.ecommerceapp.fragments.productdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import uz.otamurod.ecommerceapp.adapters.ColorsAdapter
import uz.otamurod.ecommerceapp.adapters.ProductDetailsImagesAdapter
import uz.otamurod.ecommerceapp.adapters.SizesAdapter
import uz.otamurod.ecommerceapp.data.CartProduct
import uz.otamurod.ecommerceapp.databinding.FragmentProductDetailsBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.util.hideBottomNavigationMenu
import uz.otamurod.ecommerceapp.util.showBottomNavigationMenu
import uz.otamurod.ecommerceapp.viewmodel.ProductDetailsViewModel

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val productDetailsImagesAdapter by lazy { ProductDetailsImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<ProductDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationMenu()

        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setUpColorsRecyclerView()
        setUpSizesRecyclerView()
        setUpProductImagesRecyclerView()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = String.format("$%.2f", product.price)
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
            if (product.sizes.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
        }

        binding.buttonClose.setOnClickListener {
            findNavController().navigateUp()
        }

        productDetailsImagesAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addOrUpdateProductinCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        lifecycleScope.launch {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.g_blue_gray200))
                    }
                    is Resource.Error -> {
                        binding.buttonAddToCart.revertAnimation()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpProductImagesRecyclerView() {
        binding.viewPagerProductDetails.adapter = productDetailsImagesAdapter
    }

    private fun setUpSizesRecyclerView() {
        binding.sizesRecyclerView.adapter = sizesAdapter

        sizesAdapter.onSizeClick = {
            selectedSize = it
        }
    }

    private fun setUpColorsRecyclerView() {
        binding.colorsRecyclerView.adapter = colorsAdapter

        colorsAdapter.onColorClick = {
            selectedColor = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationMenu()
    }

    companion object {
        private const val TAG = "ProductDetailsFragment"
    }
}