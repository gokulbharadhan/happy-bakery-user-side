package com.example.bakery.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.R
import com.example.bakery.adapters.CartProductAdapter
import com.example.bakery.databinding.FragmentCartBinding
import com.example.bakery.firebase.FirebaseCommon
import com.example.bakery.util.Resource
import com.example.bakery.util.VerticalItemDecoration
import com.example.bakery.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding:FragmentCartBinding
    private val cartAdapter by lazy{ CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()
        var totalprice=0f
        lifecycleScope.launchWhenStarted {

            viewModel.productPrice.collectLatest {price->
                price?.let{
                    totalprice=it
                    Log.e("lifecycle","total price updated")
                    binding.tvTotalPrice.text="₹ $price"
                }
            }
        }

        cartAdapter.onProdcutClick={
            val b=Bundle().apply{putParcelable("product",it.product)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailFragment4,b)
        }
        cartAdapter.onPlusClick={it,type->
                viewModel.changeQuantity(type,it,FirebaseCommon.QuantityChanging.INCREASE)

        }
        cartAdapter.onMinusClick={it,type->
            viewModel.changeQuantity(type,it,FirebaseCommon.QuantityChanging.DECREASE)
        }
        binding.buttonCheckout.setOnClickListener{
           val action=CartFragmentDirections.actionCartFragmentToBillingFragment(totalprice,cartAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(action)
        }
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }
        lifecycleScope.launchWhenStarted {

            viewModel.deleteDialog.collectLatest {
                val alertDialog=AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete item from the cart")
                    setNegativeButton("Cancel"){ dialog,_->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){ dialog,_ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }

                }
                alertDialog.create()
                alertDialog.show()
            }

        }
        lifecycleScope.launchWhenStarted {

            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarCart.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarCart.visibility=View.INVISIBLE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherView()
                            }else{
                                Log.e("new updation","${it.data}")
                                showOtherView()
                                hideEmptyCart()
                                cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error->{
                        binding.progressbarCart.visibility=View.VISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
    }

    private fun showOtherView() {
        binding.apply {
            rvCart.visibility=View.VISIBLE
            totalBoxContainer.visibility=View.VISIBLE
            buttonCheckout.visibility=View.VISIBLE

        }
    }

    private fun hideOtherView() {
        binding.apply {
            rvCart.visibility=View.GONE
            totalBoxContainer.visibility=View.GONE
            buttonCheckout.visibility=View.GONE

        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility=View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility=View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply{
            layoutManager=LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter=cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}