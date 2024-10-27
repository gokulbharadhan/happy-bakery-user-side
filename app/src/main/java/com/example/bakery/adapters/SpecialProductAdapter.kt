package com.example.bakery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.Product
import com.example.bakery.databinding.SpecialRvItemBinding

class SpecialProductAdapter: RecyclerView.Adapter<SpecialProductAdapter.SpecialProductsViewHolder>() {
    inner class SpecialProductsViewHolder(val binding:SpecialRvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                tvSpecialProductName.text=product.name
                tvSpecialProductPrice.text="₹ ${product.price}"
            }
        }
    }
    private val diffCallback=object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }

    }
    val differ=AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }
    var onClick:((Product) -> Unit)?=null
}