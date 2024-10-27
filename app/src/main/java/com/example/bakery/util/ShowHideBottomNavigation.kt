package com.example.bakery.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.bakery.R
import com.example.bakery.activity.shopping
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationItemView= (activity as shopping).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationItemView.visibility= View.GONE

}
fun Fragment.showBottomNavigationView(){
    val bottomNavigationItemView= (activity as shopping).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationItemView.visibility= View.VISIBLE

}