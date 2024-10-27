package com.example.bakery.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.R
import com.example.bakery.adapters.BestProductAdapter
import com.example.bakery.databinding.FragmentBaseCategoryBinding
import com.example.bakery.util.showBottomNavigationView
import java.util.zip.Inflater

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding:FragmentBaseCategoryBinding
    protected val offerAdapter:BestProductAdapter by lazy { BestProductAdapter() }
    protected  val bestProductAdapter:BestProductAdapter by lazy {BestProductAdapter()}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setUPBestProduct()

        bestProductAdapter.onClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }


        offerAdapter.onClick={
            val b= Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment4,b)
        }


        binding.rvOfferProducts.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollHorizontally(1)&& dx!=0){
                    onOfferPagingRequest()
                }
            }
        })
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_->
            if(v.getChildAt(0).bottom<=v.height+scrollY)
                onBestProductPagingRequest()
        })
    }

    open fun onOfferPagingRequest(){

    }
    open fun onBestProductPagingRequest(){

    }
fun showOfferLoading(){
    binding.offerProductProgressBar.visibility=View.VISIBLE
}
    fun showBestProductLoading(){
        binding.BestProductsProgressBar.visibility=View.VISIBLE
    }
    fun hideOfferLoading(){
        binding.offerProductProgressBar.visibility=View.GONE
    }
    fun hideBestProductLoading(){
        binding.BestProductsProgressBar.visibility=View.GONE
    }

    private fun setUPBestProduct() {
        binding.rvBestProduct.apply {
            layoutManager= GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter=bestProductAdapter
        }
    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply{
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}