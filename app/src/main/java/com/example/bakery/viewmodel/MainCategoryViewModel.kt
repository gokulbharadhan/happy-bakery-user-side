package com.example.bakery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {
    private val _specialProduct= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct:StateFlow<Resource<List<Product>>> = _specialProduct

    private val _bestDealsProduct= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProduct:StateFlow<Resource<List<Product>>> = _bestDealsProduct

    private val _bestProduct= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct:StateFlow<Resource<List<Product>>> = _bestProduct

    private val pagingInfo=PagingInfo()
    init{
        fetchSpecialProducts()
        fetchBestProduct()
        fetchBestDeals()
    }
    fun fetchSpecialProducts(){
        if(!pagingInfo.specialProductIsPagingEnd) {
            viewModelScope.launch {
                _specialProduct.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.specialProductPage*4).whereEqualTo("specialization", "Special Products").get()
                .addOnSuccessListener { result ->
                    val specialProductsList = result.toObjects(Product::class.java)
                    val specialProduct = result.toObjects(Product::class.java)
                    pagingInfo.specialProductIsPagingEnd=specialProduct==pagingInfo.oldSpecialProduct
                    pagingInfo.oldSpecialProduct=specialProduct
                    viewModelScope.launch {
                        _specialProduct.emit(Resource.Success(specialProductsList))
                    }
                    pagingInfo.specialProductPage++

                }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProduct.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }
    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsProduct.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("specialization","Best Deals").get().addOnSuccessListener {result->
            val bestDealProduct=result.toObjects(Product::class.java)
            viewModelScope.launch {
                _bestDealsProduct.emit(Resource.Success(bestDealProduct))
            }

        }.addOnFailureListener {
            viewModelScope.launch {
                _bestDealsProduct.emit(Resource.Error(it.message.toString()))
            }
        }
    }
    fun fetchBestProduct(){
        if(!pagingInfo.bestProductIsPagingEnd){
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }
            firestore.collection("Products").limit(pagingInfo.bestProductPage*10).get().addOnSuccessListener { result ->
                val bestProduct = result.toObjects(Product::class.java)
                pagingInfo.bestProductIsPagingEnd=bestProduct==pagingInfo.oldBestProduct
                pagingInfo.oldBestProduct=bestProduct
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Success(bestProduct))
                }
pagingInfo.bestProductPage++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Error(it.message.toString()))
                }
            }
            }

    }
}
internal data class PagingInfo(
    var bestProductPage:Long=1,
    var oldBestProduct:List<Product> = emptyList(),
    var bestProductIsPagingEnd:Boolean=false,
    var specialProductPage:Long=1,
    var oldSpecialProduct:List<Product> = emptyList(),
    var specialProductIsPagingEnd:Boolean=false,
)
