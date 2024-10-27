package com.example.bakery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bakery.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_login_register)
    }
}