package com.example.bakery.viewmodel

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakery.R
import com.example.bakery.util.Constent.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val fireBaseAuth:FirebaseAuth

):ViewModel() {
    private val _navigate= MutableStateFlow(0)
    val navigate:StateFlow<Int> = _navigate

    init{
        val isButtonClicked=sharedPreferences.getBoolean(INTRODUCTION_KEY,false)
        val user=fireBaseAuth.currentUser
        if(user!=null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        }else if(isButtonClicked){
            viewModelScope.launch {
                _navigate.emit(R.id.action_introductionFragment_to_accountOptionsFragment)
            }
        }else{
            Unit
        }

    }
    companion object{
        @SuppressLint("NonConstantResourceId")
        const val SHOPPING_ACTIVITY=23
    }
    fun startButtonClick(){
     sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }
}