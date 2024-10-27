package com.example.bakery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.data.Address
import com.example.bakery.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asSharedFlow()


    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!)
                .collection("address")
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener { e ->
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(e.message ?: "Unknown error")) }
                }
        } else {
            viewModelScope.launch {
                _error.emit("Fields are required")
            }
        }
    }


    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.place.trim().isNotEmpty()
    }
}