package com.example.bakery.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.R
import com.example.bakery.data.Order
import com.example.bakery.data.OrderStatus
import com.example.bakery.data.getOrderStatus
import com.example.bakery.databinding.OrderItemBinding

@Suppress("DEPRECATION")
class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "Order id :" + order.orderId.toString()
                tvOrderDate.text = "Ordered Date :"+ order.date
                tvOrderStatus.text="Order Status :"+ order.orderStatus
                val resources = itemView.resources

                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)

            }
        }
    }


    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Order) -> Unit)? = null
}