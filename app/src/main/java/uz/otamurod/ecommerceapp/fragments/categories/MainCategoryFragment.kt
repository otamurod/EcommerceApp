package uz.otamurod.ecommerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.otamurod.ecommerceapp.adapters.SpecialProductsAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentMainCategoryBinding
import uz.otamurod.ecommerceapp.util.Resource
import uz.otamurod.ecommerceapp.viewmodel.MainCategoryVIewModel

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private val viewModel by viewModels<MainCategoryVIewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
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
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun setUpRecyclerView() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.specialProductsRecyclerView.adapter = specialProductsAdapter
    }

    companion object {
        private const val TAG = "MainCategoryFragment"
    }
}