package com.example.bakery.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.CartProduct
import com.example.bakery.firebase.FirebaseCommon
import com.example.bakery.helper.getProductPrice
import com.example.bakery.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
    private val auth:FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts=_cartProducts.asStateFlow()
    private val _deleteDialog= MutableSharedFlow<CartProduct>()
    val deleteDialog= _deleteDialog.asSharedFlow()
    val productPrice=cartProducts.map{
        when(it){
            is Resource.Success->{
                calculatePrice(it.data!!)
            }
            else->null
        }
    }
    private var cartProdcutDocumnts= emptyList<DocumentSnapshot>()
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProdcutDocumnts[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }
    }


    private fun calculatePrice(data: List<CartProduct>): Float {
        var totalPrice = 0.0

        for (cartProduct in data) {
            val cartValue: Double = when {
                cartProduct.type.equals("Gram") -> cartProduct.quantity / 50.0
                else -> cartProduct.quantity // Default value for types other than "KG" or "Gram"
            }
            val actulPrice: Double = when {
                cartProduct.type.equals("KG")  -> cartProduct.product.price.toDouble()
                cartProduct.type.equals("Gram")&& !cartProduct.product.details.equals("KG & Gram") -> cartProduct.product.price.toDouble()
                cartProduct.type.equals("Piece") -> cartProduct.product.price.toDouble()
                cartProduct.type.equals("Bundle")&& !cartProduct.product.details.equals("Piece & Bundle") -> cartProduct.product.price.toDouble()
                cartProduct.type.equals("Gram")&& cartProduct.product.details.equals("KG & Gram") -> cartProduct.product.price1!!.toDouble() // 1 gram = 0.001 KG
                cartProduct.type.equals("Bundle")&& cartProduct.product.details.equals("Piece & Bundle") -> cartProduct.product.price1!!.toDouble() // 1 gram = 0.001 KG
                else -> cartProduct.product.price.toDouble()
            }

            totalPrice += cartProduct.product.offerPercentage.getProductPrice(actulPrice.toFloat()) * cartValue
        }

        return totalPrice.toFloat()
    }


    init {
        getCartProduct()
    }
     fun getCartProduct(){

        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener{ value,error->
                if(error!=null || value==null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                }else{
                    Log.e("getproduct","updated recycle")
                    cartProdcutDocumnts=value.documents
                    val cartProduct=value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProduct)) }
                }

            }
    }


    fun changeQuantity(
        type:String,
        cartProduct:CartProduct,
       quantityChanging: FirebaseCommon.QuantityChanging
    ){

        val index=cartProducts.value.data?.indexOf(cartProduct)


        if(index!=null && index!=-1) {
            val documentId = cartProdcutDocumnts[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE->{
                    if(type.equals("KG")){
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,0.5,""))
                        increaseQuantity(documentId)
                    }else if(type.equals("Gram")){
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,50.0,""))
                        increaseQuantity(documentId)
                    }
                    else{
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,1.0,""))
                        increaseQuantity(documentId)
                    }

                }
                FirebaseCommon.QuantityChanging.DECREASE->{
                    if(type == "KG"){
                        if(cartProduct.quantity==0.5){
                            Log.e("kg","deleting kg")
                            viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                            return
                        }
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,0.5,""))
                        decreaseQuantity(documentId)

                    }else if(type == "Gram"){
                        Log.e("gram","deleting gram")
                        if(cartProduct.quantity==50.0){
                            viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                            return
                        }
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,50.0,""))
                        decreaseQuantity(documentId)

                    }
                    else{
                        if(cartProduct.quantity==1.0){
                            viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                            return
                        }
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        firebaseCommon.cartdata(CartProduct(cartProduct.product,1.0,""))
                        decreaseQuantity(documentId)

                    }
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
    firebaseCommon.DecreaseQuantity(documentId){result,exception->
        if(exception!=null){
            viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString()))}
        }
    }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result,exception->
            if(exception!=null){
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString()))}
            }
        }
    }


}