package com.ibrajix.multiclock.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Alarm
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.text.SimpleDateFormat
import java.util.*

object UiUtility {

    //transparent status bar
    fun Activity.transparentStatusBar(){
        val decor = window.decorView
        decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }

    //check current theme
    fun Context.isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    //format current time
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.ROOT)
        return sdf.format(Date())
    }

    //show material dialog to enable alarm setting from fragment
    @RequiresApi(Build.VERSION_CODES.S)
    fun Fragment.showMaterialDialog(title: String, message: String, anim: Int){
        val mDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setAnimation(anim)
            .setPositiveButton(
                requireContext().getString(R.string.allow)
            ) { dialogInterface, which ->
                //on click, navigate to settings screen
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                startActivity(intent)
            }
            .setNegativeButton(
                requireContext().getString(R.string.later)
            ){dialogInterface, which ->
                //on click cancel
                dialogInterface.dismiss()
            }
            .build()
        mDialog.show()
    }

    //show material dialog to enable alarm setting from activity
    @RequiresApi(Build.VERSION_CODES.S)
    fun Activity.showMaterialDialog(title: String, message: String, anim: Int){
        val mDialog = MaterialDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setAnimation(anim)
            .setPositiveButton(
                getString(R.string.allow)
            ) { dialogInterface, which ->
                //on click, navigate to settings screen
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                startActivity(intent)
            }
            .setNegativeButton(
                getString(R.string.later)
            ){dialogInterface, which ->
                //on click cancel
                dialogInterface.dismiss()
            }
            .build()
        mDialog.show()

    }

    //show bottom sheet dialog froma
    fun Fragment.showConfirmationDeleteDialog(alarm: Alarm, title: String, message: String, callback : (Alarm) -> Unit){
        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.delete), R.drawable.ic_check
            ) { dialogInterface, which ->
                callback(alarm)
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                getString(R.string.no), R.drawable.ic_close
            ) { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            .build()

        // Show Dialog
        mBottomSheetDialog.show()
    }

}