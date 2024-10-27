package com.example.bakery.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.adapters.BillingProductAdapter
import com.example.bakery.data.OrderStatus
import com.example.bakery.data.getOrderStatus
import com.example.bakery.databinding.FragmentOrderDetailBinding
import com.example.bakery.util.VerticalItemDecoration
import com.example.bakery.util.hideBottomNavigationView

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        hideBottomNavigationView()

        setupOrderRv()

        binding.apply {

            tvOrderId.text = "Order #${order.orderId}"


            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Delivered -> 2
                else -> 0
            }

            stepView.go(currentOrderState, false)
            if (currentOrderState == 2) {
                stepView.done(true)
            }
        imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street}  ${order.address.place}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "₹ ${order.totalPrice}"

        }

        billingProductsAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}