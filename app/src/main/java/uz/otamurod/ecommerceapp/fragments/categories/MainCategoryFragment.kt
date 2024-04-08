package uz.otamurod.ecommerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.adapters.BestDealsAdapter
import uz.otamurod.ecommerceapp.adapters.BestProductsAdapter
import uz.otamurod.ecommerceapp.adapters.SpecialProductsAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentMainCategoryBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.MainCategoryVIewModel

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {
    private lateinit var binding: FragmentMainCategoryBinding
    private val viewModel by viewModels<MainCategoryVIewModel>()
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRecyclerView()
        setUpBestDealsRecyclerView()
        setUpBestProductsRecyclerView()

        lifecycleScope.launch {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, "onViewCreated: ${it.message}")
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.bestDeals.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        binding.tvBestDeals.isVisible = true
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, "onViewCreated: ${it.message}")
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
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
                        binding.tvBestProducts.isVisible = true
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, "onViewCreated: ${it.message}")
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, _, scrollY, _, _ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY) {
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun setUpSpecialProductsRecyclerView() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.specialProductsRecyclerView.adapter = specialProductsAdapter
    }

    private fun setUpBestDealsRecyclerView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.bestDealsRecyclerView.adapter = bestDealsAdapter
    }

    private fun setUpBestProductsRecyclerView() {
        bestProductsAdapter = BestProductsAdapter()
        binding.bestProductsRecyclerView.adapter = bestProductsAdapter
    }

    companion object {
        private const val TAG = "MainCategoryFragment"
    }
}