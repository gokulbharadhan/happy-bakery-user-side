package com.example.bakery.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bakery.data.User
import com.example.bakery.util.Constent.USER_COLLECTION
import com.example.bakery.util.RegisterFieldState
import com.example.bakery.util.RegisterValidation
import com.example.bakery.util.Resource
import com.example.bakery.util.validateEmail
import com.example.bakery.util.validateFristName
import com.example.bakery.util.validateLastName
import com.example.bakery.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _register= MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register
private val _validation= Channel<RegisterFieldState>()
    val validation=_validation.receiveAsFlow()
    fun createAccountWithEmailAndPassword(user: User,password:String) {
        if(checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                it.user?.let {
                    savedUserInfo(it.uid,user)

                }
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
        }else{
            val registerFieldState=RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password),
                validateFristName(user.firstName),
                validateLastName(user.lastName)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }

        }
    }

    private fun savedUserInfo(UserUid: String,user:User) {
        db.collection(USER_COLLECTION)
            .document(UserUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }

    }

    private fun checkValidation(user: User, password: String):Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val firstNameValdiate= validateFristName(user.firstName)
        val lastNameValidate= validateLastName(user.lastName)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success && firstNameValdiate is
                    RegisterValidation.Success && lastNameValidate is RegisterValidation.Success
        return shouldRegister
    }
}