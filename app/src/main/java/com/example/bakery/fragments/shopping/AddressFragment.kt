package com.example.bakery.fragments.shopping

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.bakery.data.Address
import com.example.bakery.databinding.FragmentAddressBinding
import com.example.bakery.util.Resource
import com.example.bakery.viewmodel.AddressViewModel
import com.example.bakery.viewmodel.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class AddressFragment:Fragment() {
    private lateinit var binding:FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val billViewModel by viewModels<BillingViewModel>()
    var places= arrayListOf<String>()
    var selectedSpinnerItem=""
    val args by navArgs<AddressFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarAddress.visibility=View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            billViewModel.updateSingle.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarAddress.visibility=View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val address=args.address
        var pos=0
        if(address==null){
            binding.buttonDelelte.visibility=View.GONE
        }else{
            binding.apply{
                buttonSave.text="Update"
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edPhone.setText(address.phone)
                edStreet.setText(address.street)
            }
        }

       binding.apply{
           places.add("Select an option")
           places.add("Pallikare")
           places.add("ShakthiNagar")
           places.add("Bilal")
           places.add("Movval")
           places.add("Kallingal")
           places.add("CH Nagar")
           places.add("Masthigudde")
           places.add("Thotti")
           places.add("Pakkam")
           places.add("CharalKadav")
           places.add("Kottakun")
           places.add("Killeria")
           places.add("Thekkekunn")
            imageAddressClose.setOnClickListener {
                findNavController().navigateUp()
            }
           val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, places)
           spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
           binding.edState.adapter = spinnerAdapter

           edState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
               override fun onItemSelected(
                   parent: AdapterView<*>?,
                   view: View?,
                   position: Int,
                   id: Long
               ) {
                   pos=position
                   if(position!=0) {
                       selectedSpinnerItem = places[position]
                   }
               }


               override fun onNothingSelected(p0: AdapterView<*>?) {
                   selectedSpinnerItem = ""
               }
           }
           buttonSave.setOnClickListener {
               if (address == null) {
                   if (selectchec()) {
                       if(validatePhoneNumber(edPhone.text.toString())) {
                           if(selectedSpinnerItem!="") {
                               val addressTitle = edAddressTitle.text.toString()
                               val fullName = edFullName.text.toString()
                               val street = edStreet.text.toString()
                               val phone = edPhone.text.toString()
                               val place = selectedSpinnerItem
                               val addresses = Address(addressTitle, fullName, street, phone, place)
                               Toast.makeText(requireContext(), "Address Added", Toast.LENGTH_SHORT)
                                   .show()
                               viewModel.addAddress(addresses)
                           }else {
                               Toast.makeText(
                                   requireContext(),
                                   "Select the place",
                                   Toast.LENGTH_SHORT
                               )
                                   .show()
                           }
                       }else{
                           Toast.makeText(requireContext(), "Phone number should 10 digits", Toast.LENGTH_SHORT)
                               .show()
                       }
                   } else {
                       Toast.makeText(requireContext(), "Fields are required", Toast.LENGTH_SHORT)
                           .show()
                   }

               } else {
                   if (selectchec()) {
                       if (validatePhoneNumber(edPhone.text.toString())) {
                           if(pos!=0) {
                               val addressTitle = edAddressTitle.text.toString()
                               val fullName = edFullName.text.toString()
                               val street = edStreet.text.toString()
                               val phone = edPhone.text.toString()
                               val place = selectedSpinnerItem
                               val addresses = Address(addressTitle, fullName, street, phone, place)
                               Toast.makeText(
                                   requireContext(),
                                   "Address Updated",
                                   Toast.LENGTH_SHORT
                               )
                                   .show()
                               billViewModel.updateAddressInFire(addresses, address)
                           }else{
                               Toast.makeText(requireContext(), "Select the place", Toast.LENGTH_SHORT)
                                   .show()
                           }
                       }else{
                           Toast.makeText(requireContext(), "Phone number should 10 digits", Toast.LENGTH_SHORT)
                               .show()
                       }
                   }else{
                       Toast.makeText(requireContext(), "Fields are required", Toast.LENGTH_SHORT)
                           .show()
                   }
               }
           }
           buttonDelelte.setOnClickListener {
               if (address != null) {
                   billViewModel.deleteAddress(address)
               }
               Toast.makeText(requireContext(),"Address Deleted",Toast.LENGTH_SHORT).show()
               findNavController().navigateUp()
           }
           address?.let { address ->
               val selectedPosition = places.indexOf(address.place)
               if (selectedPosition != -1) {
                   Log.d("Debug", "Selected position: $selectedPosition")
                   binding.edState.setSelection(selectedPosition)
               } else {
                   Log.d("Debug", "Selected place not found in places list: ${address.place}")
               }
           }

       }
    }

    private fun selectchec(): Boolean {
    if (binding.edAddressTitle.text.isNotEmpty()&& binding.edFullName.text.isNotEmpty()&&binding.edPhone.text.isNotEmpty()){
        return true
    }
        else{
            return false
    }
    }
    fun validatePhoneNumber(phoneNumber: String): Boolean {
        val pattern = Regex("^\\d{10}$") // Regex pattern for 10-digit phone numbers
        return pattern.matches(phoneNumber)
    }
}