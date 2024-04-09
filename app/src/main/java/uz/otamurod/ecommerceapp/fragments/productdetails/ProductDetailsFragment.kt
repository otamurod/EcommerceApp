package uz.otamurod.ecommerceapp.fragments.productdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.databinding.FragmentProductDetailsBinding

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        private const val TAG = "ProductDetailsFragment"
    }
}