package com.example.bakery.fragments.loginregister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bakery.R
import com.example.bakery.activity.shopping
import com.example.bakery.databinding.FragmentIntroductionBinding
import com.example.bakery.viewmodel.IntroductionViewModel
import com.example.bakery.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_introduction) {
    private lateinit var binding:FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{
                when(it){
                    SHOPPING_ACTIVITY-> {
                        Intent(requireActivity(), shopping::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    R.id.action_introductionFragment_to_accountOptionsFragment->{
                        findNavController().navigate(it)
                    }
                        else->Unit
                }
            }
        }
        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}