package com.example.bakery.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Product
import com.example.bakery.databinding.CartProductItemBinding
import com.example.bakery.firebase.FirebaseCommon
import com.example.bakery.helper.getProductPrice

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {
    var quantityitem:Double=0.0
    inner class CartProductViewHolder(val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root){
        lateinit var firebasecm:FirebaseCommon
        fun bind(cartProduct: CartProduct){
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text= cartProduct.product.name

                quantityitem= cartProduct.quantity
                Log.e("cartProduct","$quantityitem")
                tvCartProductQuantity.text=quantityitem.toString()
                type.text=cartProduct.type.toString()
                val productprice:Float
                var incrementval:Double
                if(cartProduct.type.equals("KG"))
                    productprice= cartProduct.product.price
                else if(cartProduct.type.equals("Gram")&& !cartProduct.product.details.equals("KG & Gram"))
                    productprice= cartProduct.product.price
                else if(cartProduct.type.equals("Piece"))
                    productprice= cartProduct.product.price
                else if(cartProduct.type.equals("Bundle")&& !cartProduct.product.details.equals("Piece & Bundle"))
                    productprice= cartProduct.product.price
                else if(cartProduct.type.equals("Gram")&& cartProduct.product.details.equals("KG & Gram"))
                    productprice= cartProduct.product.price1!!
                else if(cartProduct.type.equals("Bundle")&& cartProduct.product.details.equals("Piece & Bundle"))
                    productprice= cartProduct.product.price1!!
                else
                    productprice= cartProduct.product.price1!!
             val priceAfterPercentage=cartProduct.product.offerPercentage.getProductPrice(productprice)
                tvProductCartPrice.text="₹ ${String.format("%.2f",priceAfterPercentage)}"

            }
        }
    }
    private val diffCallback=object: DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            Log.e("areItemsThesame","are item same")
            return oldItem.product.id==newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            Log.e("areContentsTheSane","are content the same")
            return oldItem==newItem
        }

    }
    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        Log.e("onCrateView","oncrate view")
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }


    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartProduct=differ.currentList[position]
        holder.bind(cartProduct)
        Log.e("on binding","on binding")
        Log.e("cartProduct","${cartProduct.quantity}")
        holder.itemView.setOnClickListener{
            onProdcutClick?.invoke(cartProduct)
        }
        holder.binding.imagePlus.setOnClickListener{
            onPlusClick?.invoke(cartProduct, cartProduct.type!!)
        }
        holder.binding.imageMinus.setOnClickListener{
            onMinusClick?.invoke(cartProduct,cartProduct.type!!)
        }

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProdcutClick:((CartProduct) -> Unit)?=null
    var onPlusClick:((CartProduct,String) -> Unit)?=null
    var onMinusClick:((CartProduct,String) -> Unit)?=null



}