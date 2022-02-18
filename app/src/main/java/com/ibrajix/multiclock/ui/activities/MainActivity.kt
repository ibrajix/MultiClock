package com.ibrajix.multiclock.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.ActivityMainBinding
import com.ibrajix.multiclock.ui.viewmodel.BottomNavViewModel
import com.ibrajix.multiclock.utils.UiUtility.isDarkTheme
import com.ibrajix.multiclock.utils.UiUtility.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val bottomNavViewModel: BottomNavViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!isDarkTheme()){
            transparentStatusBar()
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCodes()
        handleBottomNavVisibility()

    }

    private fun handleBottomNavVisibility(){

        bottomNavViewModel.bottomNavigationVisibility.observe(this) { navVisibility ->
            binding.bottomNav.visibility = navVisibility
        }

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.alarmDetailsFragment -> bottomNavViewModel.hideBottomNav()
                else -> bottomNavViewModel.showBottomNav()
            }
        }


    }

    private fun initCodes(){

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()?.let { bottom_nav.setupWithNavController(it) }

    }

}