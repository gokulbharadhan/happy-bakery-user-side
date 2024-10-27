package com.example.bakery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
):ViewModel() {
    private val _searchProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchProducts: StateFlow<Resource<List<Product>>> = _searchProducts
init {
    fetchBestProduct()
}
    fun fetchBestProduct(){
            viewModelScope.launch {
                _searchProducts.emit(Resource.Loading())
                firestore.collection("Products").get().addOnSuccessListener { result ->
                   val bestProduct = result.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _searchProducts.emit(Resource.Success(bestProduct))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _searchProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
            }
    }


}