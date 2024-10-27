package com.example.bakery.fragments.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bakery.R
import com.example.bakery.activity.shopping
import com.example.bakery.databinding.FragmentLoginBinding
import com.example.bakery.dialog.setupBottomSheetDialog
import com.example.bakery.util.Resource
import com.example.bakery.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding:FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.apply {
            loginloginButton.setOnClickListener{
                val email=edEmailLogin.text.toString().trim()
                val password=edPasswordLogin.text.toString()
                if(email.isEmpty()){
                    binding.edEmailLogin.apply {
                        requestFocus()
                        error="Gmail is required"
                    }
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.edEmailLogin.apply {
                        requestFocus()
                        error="Enter the correct gmail format"
                    }
                }
                else if(password.isEmpty()){
                    binding.edPasswordLogin.apply {
                        requestFocus()
                        error="Password is required"
                    }
                }else {
                    viewModel.login(email, password)
                }
        }

        }
        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email->
                viewModel.resetPassword(email)


            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading->{

                    }
                    is Resource.Success->{
                        Snackbar.make(requireView(),"Reset link was sent to your email",Snackbar.LENGTH_LONG).show()
                        }
                    is Resource.Error->{
                        Snackbar.make(requireView(),"Error:${it.message}",Snackbar.LENGTH_LONG).show()
                    }
                    else-> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading->{
                        binding.loginloginButton.startAnimation()
                    }
                    is Resource.Success->{
                        binding.loginloginButton.revertAnimation()
                        Intent(requireActivity(),shopping::class.java).also{intent->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        binding.loginloginButton.revertAnimation()

                    }
                    else-> Unit
                }
            }
        }
    }
}