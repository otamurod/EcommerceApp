package uz.otamurod.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.adapters.BestProductsAdapter
import uz.otamurod.ecommerceapp.adapters.OfferProductsAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentBaseCategoryBinding

@AndroidEntryPoint
open class BaseCategoryFragment : Fragment() {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val offerProductsAdapter: OfferProductsAdapter by lazy { OfferProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOfferRecyclerView()
        setUpBestProductsRecyclerView()

        binding.offerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferProductsPagingRequest()
                }
            }
        })

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, _, scrollY, _, _ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY) {
                onBestProductsPagingRequest()
            }
        })

    }

    open fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    open fun showLoading() {
        binding.progressBar.isVisible = true
    }

    open fun onOfferProductsPagingRequest() {}

    open fun onBestProductsPagingRequest() {}

    private fun setUpOfferRecyclerView() {
        binding.offerRecyclerView.adapter = offerProductsAdapter

        offerProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
    }

    private fun setUpBestProductsRecyclerView() {
        binding.bestProductsRecyclerView.adapter = bestProductsAdapter

        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
    }

}