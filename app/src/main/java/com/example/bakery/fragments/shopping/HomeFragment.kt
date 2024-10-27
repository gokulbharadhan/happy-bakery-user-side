package com.example.bakery.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bakery.R
import com.example.bakery.adapters.HomeViewPagerAdapter
import com.example.bakery.databinding.FragmentHomeBinding
import com.example.bakery.fragments.categories.BreadFragment
import com.example.bakery.fragments.categories.CakesFragment
import com.example.bakery.fragments.categories.ChipsFragment
import com.example.bakery.fragments.categories.ChoclateFragment
import com.example.bakery.fragments.categories.CoolDrinks
import com.example.bakery.fragments.categories.MainCategory
import com.example.bakery.fragments.categories.MixtureFragment
import com.example.bakery.fragments.categories.IceCream
import com.example.bakery.fragments.categories.SnacksFragment
import com.example.bakery.fragments.categories.SweetsFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragment= arrayListOf<Fragment>(
            MainCategory(),
            BreadFragment(),
            CakesFragment(),
            ChipsFragment(),
            ChoclateFragment(),
            MixtureFragment(),
            SnacksFragment(),
            SweetsFragment(),
            IceCream(),
            CoolDrinks(),


        )
        binding.viewpagerhome.isUserInputEnabled=false
        val viewPager2Adapter=HomeViewPagerAdapter(categoriesFragment,childFragmentManager,lifecycle)
        binding.viewpagerhome.adapter=viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewpagerhome){tab,position->
            when(position){
                0->tab.text="Home"
                1->tab.text="Bread"
                2->tab.text="Cakes"
                3->tab.text="Chips"
                4->tab.text="Chocolates"
                5->tab.text="Mixture"
                6->tab.text="Snacks"
                7->tab.text="Sweets"
                8->tab.text="Ice Cream"
                9->tab.text="Cool Drinks"
            }
        }.attach()

    }

}