package com.example.bakery.fragments.shopping

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.R
import com.example.bakery.adapters.AddressAdpater
import com.example.bakery.adapters.BillingProductAdapter
import com.example.bakery.data.Address
import com.example.bakery.data.CartProduct
import com.example.bakery.data.Order
import com.example.bakery.data.OrderStatus
import com.example.bakery.databinding.FragmentBillingBinding
import com.example.bakery.util.HorizontalItemDecoration
import com.example.bakery.util.Resource
import com.example.bakery.viewmodel.BillingViewModel
import com.example.bakery.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class BillingFragment: Fragment() {

    private lateinit var binding:FragmentBillingBinding
    private val addressAdapter by lazy{AddressAdpater()}
    private val billingProductAdapter by lazy{BillingProductAdapter()}
    private val billingViewModel: BillingViewModel by viewModels<BillingViewModel> ()
    private val orderViewmodel by viewModels<OrderViewModel> ()
    private val args by navArgs<BillingFragmentArgs>()
    private var product= emptyList<CartProduct>()
    private var totalPrice=0f
    private var selectedAddress:Address?=null
    private var selectedDate:String?=null
    private var selectedTime:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product=args.product.toList()
        totalPrice=args.totolPrice
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentBillingBinding.inflate(inflater)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingProductRv()
        setupAddressRv()
        if(!args.payment){
            binding.apply {
                buttonPlaceOrder.visibility=View.INVISIBLE
                totalBoxContainer.visibility=View.INVISIBLE
                middleLine.visibility=View.INVISIBLE
                bottomLine.visibility=View.INVISIBLE
                imagetime.visibility=View.INVISIBLE
                imageCalander.visibility=View.INVISIBLE
                timepick.visibility=View.INVISIBLE
                datepick.visibility=View.INVISIBLE
                tvdateandtime.visibility=View.INVISIBLE

            }
        }
        val myCalendar=Calendar.getInstance()
        val mytime=Calendar.getInstance()
        val datePicker=DatePickerDialog.OnDateSetListener { view, year, month, dayofmonth ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayofmonth)
            updateLabel(myCalendar)
        }
        val timePicker=TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->  
            mytime.set(Calendar.HOUR_OF_DAY,hour)
            mytime.set(Calendar.MINUTE,minute)
            updateTimeText(mytime)
        }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            datePicker,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }
        datePickerDialog.datePicker.minDate = myCalendar.timeInMillis
        binding.imageCalander.setOnClickListener {
            datePickerDialog.show()
        }
        binding.imagetime.setOnClickListener {
            TimePickerDialog(requireContext(),timePicker,mytime.get(Calendar.HOUR_OF_DAY),mytime.get(Calendar.MINUTE),false).show()
        }
        binding.imageAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }
        billingProductAdapter.onClick={
            val b=Bundle().apply{putParcelable("product",it.product)}
            findNavController().navigate(R.id.action_billingFragment_to_cartFragment,b)
        }
        lifecycleScope.launchWhenStarted {
            orderViewmodel.order.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success->{
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Your order is placed",Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error->{
                        binding.buttonPlaceOrder.startAnimation()
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            billingViewModel.address.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility=View.INVISIBLE
                    }
                    is Resource.Error->{
                        binding.progressbarAddress.visibility=View.VISIBLE
                        Toast.makeText(requireContext(),"Error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }

        }
        billingProductAdapter.differ.submitList(product)
        binding.tvTotalPrice.text="$totalPrice"
        addressAdapter.onClick={
            selectedAddress=it
            if(!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment,b)
            }
        }
        binding.buttonPlaceOrder.setOnClickListener {
            if(selectedAddress==null){
                Toast.makeText(requireContext(),"Please select any address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(selectedDate==null){
                Toast.makeText(requireContext(),"Please select the date",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(selectedTime==null){
                Toast.makeText(requireContext(),"Please select the Time",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showConfimationDailogue()

        }

    }

    private fun showConfimationDailogue() {
        val alertDialog= AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order cart items ?")
            setNegativeButton("Cancel"){ dialog,_->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){ dialog,_ ->
                val order= Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    selectedDate!!,
                    selectedTime!!,
                    product,
                    selectedAddress!!,
                    userId = ""
                )
                orderViewmodel.placeOrder(order)
                dialog.dismiss()
            }

        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun updateTimeText(mytime: Calendar) {
        val myFormat = "hh:mm a"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.timepick.setText(sdf.format(mytime.time))
        selectedTime=sdf.format(mytime.time)
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat="dd-MM-yyyy"
        val sdf=SimpleDateFormat(myFormat, Locale.UK)
        binding.datepick.setText(sdf.format(myCalendar.time))
        selectedDate=sdf.format(myCalendar.time)
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply{
            layoutManager=LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter=addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductRv() {
        binding.rvProducts.apply{
            layoutManager=LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter=billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}