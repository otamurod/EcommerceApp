package uz.otamurod.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.otamurod.ecommerceapp.adapters.BillingProductsAdapter
import uz.otamurod.ecommerceapp.data.order.OrderStatus
import uz.otamurod.ecommerceapp.data.order.getOrderStatus
import uz.otamurod.ecommerceapp.databinding.FragmentOrderDetailBinding

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val args by navArgs<OrderDetailFragmentArgs>()
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setUpProductsRecyclerView()

        binding.apply {
            if (order != null) {
                tvOrderId.text = "Order #${order.orderId}"

                stepView.setSteps(
                    mutableListOf(
                        OrderStatus.Ordered.status,
                        OrderStatus.Confirmed.status,
                        OrderStatus.Shipped.status,
                        OrderStatus.Delivered.status
                    )
                )

                val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> 0
                    is OrderStatus.Confirmed -> 1
                    is OrderStatus.Shipped -> 2
                    is OrderStatus.Delivered -> 3
                    else -> 0
                }

                stepView.go(currentOrderState, false)
                if (currentOrderState == 3) {
                    stepView.done(true)
                }

                tvFullName.text = order.address.fullName
                tvAddress.text = "${order.address.street}, ${order.address.city}"
                tvPhoneNumber.text = order.address.phoneNumber

                tvTotalPrice.text = String.format("$%.2f", order.totalPrice)

                billingProductsAdapter.differ.submitList(order.cartProducts)
            }
        }

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpProductsRecyclerView() {
        binding.rvProducts.adapter = billingProductsAdapter
    }

    companion object {
        private const val TAG = "OrderDetailFragment"
    }
}