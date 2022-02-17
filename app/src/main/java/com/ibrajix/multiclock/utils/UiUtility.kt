package com.ibrajix.multiclock.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
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

}