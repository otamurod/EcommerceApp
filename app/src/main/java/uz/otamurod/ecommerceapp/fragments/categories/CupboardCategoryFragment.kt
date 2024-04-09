package uz.otamurod.ecommerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.data.Category
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.CategoryViewModel
import uz.otamurod.ecommerceapp.viewmodel.factory.CategoryViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class CupboardCategoryFragment : BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        CategoryViewModelFactory(firestore, Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        offerProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, "onViewCreated: ${it.message}")
                        Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, "onViewCreated: ${it.message}")
                        Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onOfferProductsPagingRequest() {
        lifecycleScope.launch {
            viewModel.fetchOfferProducts()
        }
    }

    override fun onBestProductsPagingRequest() {
        lifecycleScope.launch {
            viewModel.fetchBestProducts()
        }
    }

    companion object {
        private const val TAG = "CupboardCategory"
    }
}