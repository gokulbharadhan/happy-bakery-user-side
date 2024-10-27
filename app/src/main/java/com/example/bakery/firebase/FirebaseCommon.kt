package com.example.bakery.firebase

import android.util.Log
import com.example.bakery.data.CartProduct
import com.example.bakery.databinding.FragmentProductDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FirebaseCommon(
    private val firestore:FirebaseFirestore,
    private val auth:FirebaseAuth,
) {
    lateinit var cart:CartProduct
    fun cartdata(cartProduct: CartProduct){
        cart=cartProduct
    }

    private val cartCollection=firestore.collection("user").document(auth.uid!!).collection("cart")
    fun addProductToCart(cartProduct:CartProduct,onResult:(CartProduct?,Exception?)->Unit){

        cartCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct,null)
        }.addOnFailureListener{
            onResult(null,it)
        }
    }
    fun increaseQuantity(documentId:String,onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transaction->
            val documentRef=cartCollection.document(documentId)
            val document=transaction.get(documentRef)
            val productObject=document.toObject(CartProduct::class.java)
            productObject?.let{cartProduct->
                when (cartProduct.type) {
                    "Gram" -> {
                        val newQuantity=cartProduct.quantity+cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                        Log.e("cartCollection","${cart.quantity}")
                    }
                    "KG" -> {
                        val newQuantity=cartProduct.quantity+cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                        Log.e("cartCollection","${cart.quantity}")
                    }
                    else -> {
                        val newQuantity=cartProduct.quantity+cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                        Log.e("cartCollection","${cart.quantity}")
                    }
                }
            }

        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener{
            onResult(null,it)
        }
    }
    fun DecreaseQuantity(documentId:String,onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transaction->
            val documentRef=cartCollection.document(documentId)
            val document=transaction.get(documentRef)
            val productObject=document.toObject(CartProduct::class.java)
            productObject?.let{cartProduct->
                Log.e("cartCollection","${cart.quantity}")
                when (cartProduct.type) {
                    "Gram" -> {
                        val newQuantity=cartProduct.quantity-cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                    }
                    "KG" -> {
                        val newQuantity=cartProduct.quantity-cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                    }
                    else -> {
                        val newQuantity=cartProduct.quantity-cart.quantity
                        val newProductObject=cartProduct.copy(quantity=newQuantity)
                        transaction.set(documentRef,newProductObject)
                    }
                }
            }

        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener{
            onResult(null,it)
        }
    }
    enum class QuantityChanging{
        INCREASE,DECREASE
    }
}