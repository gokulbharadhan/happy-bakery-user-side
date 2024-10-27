package com.example.bakery.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.Product
import com.example.bakery.databinding.BestDealsRvItemBinding

class BestDealAdapter: RecyclerView.Adapter<BestDealAdapter.BestDealViewHolder>() {
    inner class BestDealViewHolder(val binding:BestDealsRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                product.offerPercentage?.let{
                    val remainingPricePercentage=1f - it
                    val priceAfterOffer=remainingPricePercentage*product.price
                    tvnewprice.text="₹ ${String.format("%.2f",priceAfterOffer)}"
                    tvoldprice.paintFlags= Paint.STRIKE_THRU_TEXT_FLAG
                }
                if(product.offerPercentage==null)
                    tvnewprice.text=""

                tvoldprice.text="₹ ${String.format("%.2f",product.price)}"
                tvDealProductName.text=product.name
            }
        }
    }
    private val diffCallback=object:DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id==newItem.id

        }

    }
    val differ=AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealViewHolder {
        return BestDealViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
val product=differ.currentList[position]
        holder.bind(product)
        holder.binding.btnSeeProduct.setOnClickListener {
            onbtnClick?.invoke(product)
        }
        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }
    var onClick:((Product) -> Unit)?=null
    var onbtnClick:((Product)->Unit)?=null
}