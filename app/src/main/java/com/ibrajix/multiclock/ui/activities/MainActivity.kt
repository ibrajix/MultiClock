package com.ibrajix.multiclock.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.utils.UiUtility.isDarkTheme
import com.ibrajix.multiclock.utils.UiUtility.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        if (!isDarkTheme()){
            transparentStatusBar()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCodes()

    }

    private fun initCodes(){

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()?.let { bottom_nav.setupWithNavController(it) }

    }

}