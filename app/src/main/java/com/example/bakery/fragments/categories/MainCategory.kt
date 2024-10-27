package com.example.bakery.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakery.R
import com.example.bakery.adapters.BestDealAdapter
import com.example.bakery.adapters.BestProductAdapter
import com.example.bakery.adapters.SpecialProductAdapter
import com.example.bakery.databinding.FragmentMainCategoryBinding
import com.example.bakery.util.Resource
import com.example.bakery.util.showBottomNavigationView
import com.example.bakery.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
private val TAG="MainCategory"
@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainCategory: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding:FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter:SpecialProductAdapter
    private lateinit var bestDealsAdapter:BestDealAdapter
    private lateinit var bestProductAdapter:BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        setupBestDealRv()
        setUpBestProductRv()

        specialProductsAdapter.onClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }
        bestProductAdapter.onClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }
        bestDealsAdapter.onClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }
        bestDealsAdapter.onbtnClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.specialProduct.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.specialProductProgressBar.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        specialProductsAdapter.differ.submitList(it.data)
                        binding.specialProductProgressBar.visibility=View.GONE
                    }
                    is Resource.Error->{
                        binding.specialProductProgressBar.visibility=View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProduct.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Success->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()

                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.BestProductProgressBar.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        bestProductAdapter.differ.submitList(it.data)
                        binding.BestProductProgressBar.visibility=View.GONE
                    }
                    is Resource.Error->{
                        binding.BestProductProgressBar.visibility=View.GONE

                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_->
            if(v.getChildAt(0).bottom<=v.height+scrollY){
                viewModel.fetchBestProduct()
            }
        })
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrollX,_,_->
            if(v.getChildAt(0).right<=v.width+scrollX){
                viewModel.fetchSpecialProducts()
            }
        })
    }

    private fun setUpBestProductRv() {
        bestProductAdapter= BestProductAdapter()
        binding.rvBestProduct.apply {
            layoutManager=GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter=bestProductAdapter
        }
    }

    private fun setupBestDealRv() {
        bestDealsAdapter= BestDealAdapter()
        binding.rvBestDealsProduct.apply {
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility=View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility=View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter= SpecialProductAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=specialProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}