package com.ibrajix.multiclock.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.utils.Utility
import com.ibrajix.multiclock.utils.Utility.isDarkTheme
import com.ibrajix.multiclock.utils.Utility.transparentStatusBar
import kotlinx.android.synthetic.main.activity_main.*

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