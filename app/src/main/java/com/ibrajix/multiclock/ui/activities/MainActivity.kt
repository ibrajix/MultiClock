package com.ibrajix.multiclock.ui.activities

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.ActivityMainBinding
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.ui.viewmodel.BottomNavViewModel
import com.ibrajix.multiclock.utils.AlarmUtility
import com.ibrajix.multiclock.utils.AlarmUtility.showMaterialDialog
import com.ibrajix.multiclock.utils.UiUtility.isDarkTheme
import com.ibrajix.multiclock.utils.UiUtility.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val bottomNavViewModel: BottomNavViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!isDarkTheme()){
            transparentStatusBar()
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAlarmStatusAndSet()
        initCodes()
        handleBottomNavVisibility()

    }

    private fun checkAlarmStatusAndSet(){

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmViewModel.getAllAlarms()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.getAllAlarmsResult.collect {
                    for (alarm in it) {
                        if (alarm.status == true && alarm.weeklyRecurring == false) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if(alarmManager.canScheduleExactAlarms()){
                                    AlarmUtility.rescheduleAlarm(alarm, this@MainActivity)
                                }
                                else{
                                    //show a dialog for user to navigate to settings and turn on alarms and reminder
                                    showMaterialDialog(title = getString(R.string.need_permission), message = getString(R.string.permission_helper), anim = R.raw.permission)
                                }
                            }
                            else{
                                AlarmUtility.rescheduleAlarm(alarm, this@MainActivity)
                            }
                        }
                    }
                }
            }
        }

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