package com.example.bakery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Category
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val fireStore:FirebaseFirestore,
    private val category: Category
) : ViewModel(){
    private val _offerProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts=_offerProducts.asStateFlow()
    private val _bestProduct= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct=_bestProduct.asStateFlow()
init{
    fetchBestProduct()
    fetchOfferProduct()
}
fun fetchOfferProduct() {
    viewModelScope.launch {
        _offerProducts.emit(Resource.Loading())
    }
    fireStore.collection("Products").whereEqualTo("category",category.category)
        .whereNotEqualTo("specialization","Best Products").get()
        .addOnSuccessListener {
            val products=it.toObjects(Product::class.java)
            viewModelScope.launch {
                _offerProducts.emit(Resource.Success(products))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _offerProducts.emit(Resource.Error(it.message.toString()))
            }
        }
}

    fun fetchBestProduct() {
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }
        fireStore.collection("Products").whereEqualTo("category",category.category)
            .whereEqualTo("specialization","Best Products").get()
            .addOnSuccessListener {
                val products=it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}