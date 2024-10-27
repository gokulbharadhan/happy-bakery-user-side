package com.example.bakery.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Address
import com.example.bakery.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth
): ViewModel() {
    private val _address=MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    var address=_address.asStateFlow()

    private val _updateSingle = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val updateSingle = _updateSingle.asSharedFlow()
    private var addressDocument = emptyList<DocumentSnapshot>()
    init {
        getUserAddress()
    }
    fun getUserAddress(){
        viewModelScope.launch { _address.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener{value,error->
                if(error!=null){
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                addressDocument = value!!.documents
                val adrresses=value.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(adrresses)) }

            }
    }
    fun updateAddressInFire(addresses: Address,addressess1:Address) {
        val index = address.value.data?.indexOf(addressess1)
        if (index != null && index != -1) {
            val documentId = addressDocument[index].id
            viewModelScope.launch { _updateSingle.emit(Resource.Loading()) }
            firestore.runTransaction { transaction ->

                val collection =
                    firestore.collection("user").document(auth.uid!!).collection("address")
                val documentRef=collection.document(documentId)
                transaction.set(documentRef, addresses)
            }.addOnSuccessListener {
                viewModelScope.launch { _updateSingle.emit(Resource.Success(addresses)) }
            }.addOnFailureListener {e->
                viewModelScope.launch { _updateSingle.emit(Resource.Error(e.message ?: "Unknown error")) }

            }
        }
    }
    fun deleteAddress(addresses: Address) {
        val index = address.value.data?.indexOf(addresses)
        if (index != null && index != -1) {
            val documentId = addressDocument[index].id
            firestore.collection("user").document(auth.uid!!).collection("address").document(documentId).delete()
        }
    }
}