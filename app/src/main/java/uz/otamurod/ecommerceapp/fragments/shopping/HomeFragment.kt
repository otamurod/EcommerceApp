package uz.otamurod.ecommerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.adapters.HomeViewPagerAdapter
import uz.otamurod.ecommerceapp.databinding.FragmentHomeBinding
import uz.otamurod.ecommerceapp.fragments.categories.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewPagerAdapter: HomeViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairCategoryFragment(),
            CupboardCategoryFragment(),
            TableCategoryFragment(),
            AccessoryCategoryFragment(),
            FurnitureCategoryFragment()
        )

        homeViewPagerAdapter =
            HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = homeViewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Main"
                }

                1 -> {
                    tab.text = "Chair"
                }

                2 -> {
                    tab.text = "Cupboard"
                }

                3 -> {
                    tab.text = "Table"
                }

                4 -> {
                    tab.text = "Accessory"
                }

                5 -> {
                    tab.text = "Furniture"
                }
            }
        }.attach()
    }
}