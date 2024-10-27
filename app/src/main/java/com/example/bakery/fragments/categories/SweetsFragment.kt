package com.example.bakery.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bakery.data.Category
import com.example.bakery.util.Resource
import com.example.bakery.viewmodel.CategoryViewModel
import com.example.bakery.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SweetsFragment:BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel>{
        BaseCategoryViewModelFactory(firestore, Category.Sweets)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showOfferLoading()
                    }
                    is Resource.Success->{
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }
                    else-> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showBestProductLoading()
                    }
                    is Resource.Success->{
                        hideBestProductLoading()
                        bestProductAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }
                    else-> Unit
                }
            }
        }
    }
}