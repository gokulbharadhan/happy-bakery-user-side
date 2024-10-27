package com.example.bakery.fragments.shopping

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bakery.adapters.ViewPager2Images
import com.example.bakery.data.CartProduct
import com.example.bakery.databinding.FragmentProductDetailBinding
import com.example.bakery.util.Resource
import com.example.bakery.util.hideBottomNavigationView
import com.example.bakery.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment: Fragment(){
   private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding:FragmentProductDetailBinding
    private val viewPageAdapter by lazy{ ViewPager2Images() }
    var itemCount=0.0
    var selectedSpinnerItem="KG"
    var sizes= arrayListOf<String>()
    private val ViewModel by viewModels<DetailsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       hideBottomNavigationView()
        binding= FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product= args.product
//        setUpSizesRv()
        setUpViewPagerRv()
        binding.imageCloseButton.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.apply {
            tvProductName.text=product.name
            tvProductDescription.text=product.description
            val size = product.details
            if(size.equals("KG") || size.equals("Gram") || size.equals("Piece") || size.equals("Bundle"))
                sizes.add(size)
            else if(size.equals("KG & Gram")){
                sizes.add("KG")
                sizes.add("Gram")
            }else{
                sizes.add("Piece")
                sizes.add("Bundle")
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSpinnerItem = sizes[position]
                    //for kg
                    if(size.equals("KG & Gram")||size.equals("Piece & Bundle")){
                        if(selectedSpinnerItem.equals("KG")){
                            tvProductPrice.text="₹ ${product.price}/KG"
                            itemCount=1.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+0.5
                                if(itemCount%1==0.0)
                                    binding.tvnum.text="${itemCount.toInt()}"
                                else
                                    binding.tvnum.text="${itemCount}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 0.5
                                    if(itemCount%1==0.0)
                                        binding.tvnum.text="${itemCount.toInt()}"
                                    else
                                        binding.tvnum.text="${itemCount}"
                                    binding.tvnum.text = "${itemCount}"
                                }
                            }
                        }
                        //for piece and bundle
                        else if(selectedSpinnerItem.equals("Piece")||selectedSpinnerItem.equals("Bundle")){
                            if(selectedSpinnerItem.equals("Piece"))
                                tvProductPrice.text="₹ ${product.price}/Piece"
                            else
                                tvProductPrice.text="₹ ${product.price1}/Bundle"
                            itemCount=1.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+1
                                binding.tvnum.text="${itemCount.toInt()}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 1
                                    binding.tvnum.text = "${itemCount.toInt()}"
                                }
                            }
                        }
                        //gram
                        else if(selectedSpinnerItem.equals("Gram")){
                            tvProductPrice.text="₹ ${product.price1}/Gram"
                            itemCount=50.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+50.0

                                binding.tvnum.text="${itemCount.toInt()}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 50.0

                                    binding.tvnum.text = "${itemCount.toInt()}"
                                }
                            }
                        }

                    }
                    else{
                        if(selectedSpinnerItem.equals("KG")){
                            tvProductPrice.text="₹ ${product.price}/KG"
                            itemCount=1.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+0.5
                                if(itemCount%1==0.0)
                                    binding.tvnum.text="${itemCount.toInt()}"
                                else
                                    binding.tvnum.text="${itemCount}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 0.5
                                    if(itemCount%1==0.0)
                                        binding.tvnum.text="${itemCount.toInt()}"
                                    else
                                        binding.tvnum.text="${itemCount}"
                                    binding.tvnum.text = "${itemCount}"
                                }
                            }
                        }
                        //for piece and bundle
                        else if(selectedSpinnerItem.equals("Piece")||selectedSpinnerItem.equals("Bundle")){
                            if(selectedSpinnerItem.equals("Piece"))
                                tvProductPrice.text="₹ ${product.price}/Piece"
                            else
                                tvProductPrice.text="₹ ${product.price}/Bundle"
                            itemCount=1.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+1
                                binding.tvnum.text="${itemCount.toInt()}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 1
                                    binding.tvnum.text = "${itemCount.toInt()}"
                                }
                            }
                        }
                        //gram
                        else if(selectedSpinnerItem.equals("Gram")){
                            tvProductPrice.text="₹ ${product.price}/Gram"
                            itemCount=50.0
                            binding.tvnum.text="${itemCount.toInt()}"
                            increment.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                itemCount=itemCount2+50.0

                                binding.tvnum.text="${itemCount.toInt()}"
                            }
                            decrement.setOnClickListener {
                                val itemCount2=binding.tvnum.text.toString().toDouble()
                                if(itemCount2 > 1) {
                                    itemCount = itemCount2 - 50.0

                                    binding.tvnum.text = "${itemCount.toInt()}"
                                }
                            }
                        }
                    }

                    binding.buttonAddToCart.setOnClickListener{
                        var spinneritem=selectedSpinnerItem
                        var itemsnum=itemCount
                        ViewModel.addUpdateProductInCart(CartProduct(product,itemsnum,spinneritem))
                    }
                    lifecycleScope.launchWhenStarted {
                        ViewModel.addToCart.collectLatest {
                            when(it){
                                is Resource.Loading->{
                                    binding.buttonAddToCart.startAnimation()
                                }
                                is Resource.Success->{
                                    binding.buttonAddToCart.revertAnimation()
                                }
                                is Resource.Error->{
                                    binding.buttonAddToCart.stopAnimation()
                                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                                }
                                else->Unit
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Implement logic for nothing selected, if needed
                }
            }

            val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, sizes)
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter
        }
        viewPageAdapter.differ.submitList(product.images)
    }

    private fun setUpViewPagerRv() {
        binding.apply {
            viewPagerProductImages.adapter=viewPageAdapter
        }
    }


//    private fun setUpSizesRv() {
//        binding.rvSize.apply {
//            adapter=sizeAdapter
//            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
//        }
//    }
}