package com.example.bakery.fragments.loginregister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bakery.R
import com.example.bakery.data.User
import com.example.bakery.databinding.FragmentRegisterBinding
import com.example.bakery.util.RegisterValidation
import com.example.bakery.util.Resource
import com.example.bakery.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG="RegisterFragment"
@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment: Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.apply {
            registerRegisterButton.setOnClickListener {
                val user = User(
                    firstname.text.toString().trim(),
                    lastname.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.registerRegisterButton.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.registerRegisterButton.revertAnimation()
                        Toast.makeText(requireContext(),"Successfully Registered",Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                        Log.e(TAG, it.message.toString())
                        binding.registerRegisterButton.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.firstName is RegisterValidation.Failed) {
                withContext(Dispatchers.Main) {
                    binding.firstname.apply {
                        requestFocus()
                        error = validation.firstName.message
                    }
                }
            }else if (validation.SecondName is RegisterValidation.Failed) {
                withContext(Dispatchers.Main) {
                    binding.lastname.apply {
                        requestFocus()
                        error = validation.SecondName.message
                    }
                }
            }

                else if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                else if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}