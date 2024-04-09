package uz.otamurod.ecommerceapp.fragments.productdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.adapters.ColorsAdapter
import uz.otamurod.ecommerceapp.adapters.ProductDetailsImagesAdapter
import uz.otamurod.ecommerceapp.adapters.SizesAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentProductDetailsBinding
import uz.otamurod.ecommerceapp.util.hideBottomNavigationMenu
import uz.otamurod.ecommerceapp.util.showBottomNavigationMenu

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val productDetailsImagesAdapter by lazy { ProductDetailsImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val args by navArgs<ProductDetailsFragmentArgs>()

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
    }

    private fun setUpProductImagesRecyclerView() {
        binding.viewPagerProductDetails.adapter = productDetailsImagesAdapter
    }

    private fun setUpSizesRecyclerView() {
        binding.sizesRecyclerView.adapter = sizesAdapter
    }

    private fun setUpColorsRecyclerView() {
        binding.colorsRecyclerView.adapter = colorsAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationMenu()
    }

    companion object {
        private const val TAG = "ProductDetailsFragment"
    }
}