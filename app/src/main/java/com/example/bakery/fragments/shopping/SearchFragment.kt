package com.example.bakery.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.R
import com.example.bakery.adapters.SearchAdapter
import com.example.bakery.data.Product
import com.example.bakery.databinding.FragmentSearchBinding
import com.example.bakery.util.Resource
import com.example.bakery.util.VerticalItemDecoration
import com.example.bakery.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest

class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter by lazy { SearchAdapter() }
    private val viewModel by activityViewModels<SearchViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()
        var totalprice = 0f
        lifecycleScope.launchWhenStarted {


            searchAdapter.onProdcutClick = {
                val b = Bundle().apply { putParcelable("product", it) }
                findNavController().navigate(R.id.action_searchFragment_to_productDetailFragment4, b)
            }
            lifecycleScope.launchWhenStarted {

                viewModel.searchProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            if (it.data!!.isEmpty()) {
                            } else {
                                Log.e("new updation", "${it.data}")
//                                searchAdapter.differ.submitList(it.data)

                            }
                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
            binding.imageCloseCart.setOnClickListener {
                findNavController().navigateUp()
            }
         binding.searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{

             override fun onQueryTextSubmit(query: String?): Boolean {
                return false
             }

             override fun onQueryTextChange(newText: String?): Boolean {

                searchList(newText)
                 return true
             }

         })
        }
    }

    private fun searchList(newText: String?) {

        lifecycleScope.launchWhenStarted {

            viewModel.searchProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                        } else {
                            val searchList=ArrayList<Product>()
                            for(dataclass in it.data){
                                if(newText?.lowercase()?.let { dataclass.name.lowercase()?.contains(it) } ==true)
                                    searchList.add(dataclass)

                            }
                            searchAdapter.differ.submitList(searchList)


                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

    }


    private fun setupCartRv() {
            binding.rvProducts.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = searchAdapter
                addItemDecoration(VerticalItemDecoration())
            }
        }
}