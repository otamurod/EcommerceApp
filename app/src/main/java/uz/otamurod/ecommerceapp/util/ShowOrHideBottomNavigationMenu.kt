package uz.otamurod.ecommerceapp.util

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.activities.ShoppingActivity

fun Fragment.hideBottomNavigationMenu() {
    val bottomNavigationMenu =
        (requireActivity() as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationMenu.isVisible = false
}

fun Fragment.showBottomNavigationMenu() {
    val bottomNavigationMenu =
        (requireActivity() as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationMenu.isVisible = true
}

