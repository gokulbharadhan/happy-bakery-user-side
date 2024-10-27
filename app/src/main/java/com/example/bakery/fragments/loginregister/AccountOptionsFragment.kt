package com.example.bakery.fragments.loginregister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DispatchMode
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakery.R
import com.example.bakery.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment: Fragment(R.layout.fragment_account_options) {
    private lateinit var binding:FragmentAccountOptionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
        }
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
        }
        val onBackPressedCallback=object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.accountOptionsFragment) {
                    // If it is, finish the activity
                    requireActivity().finish()
                } else {
                    // Otherwise, let the system handle the back button press
                    isEnabled = false
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)


    }

}