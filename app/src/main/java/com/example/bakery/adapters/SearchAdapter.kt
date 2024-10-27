package com.example.bakery.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Product
import com.example.bakery.databinding.SearchItemBinding
import com.example.bakery.firebase.FirebaseCommon


class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(val binding:SearchItemBinding): RecyclerView.ViewHolder(binding.root){
        lateinit var firebasecm: FirebaseCommon
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageCartProduct)
                tvProductCartName.text= product.name
                tvProductCartPrice.text="₹ ${product.price}"
                type.text=product.details


            }
        }
    }
    private val diffCallback=object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            Log.e("areItemsThesame","are item same")
            return oldItem.id==newItem.id
        }



        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }

    }

    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        Log.e("onCrateView","oncrate view")
        return SearchViewHolder(
            SearchItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onProdcutClick?.invoke(product)
        }

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProdcutClick:((Product) -> Unit)?=null


}