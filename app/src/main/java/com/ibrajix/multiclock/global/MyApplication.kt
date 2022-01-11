package com.ibrajix.multiclock.global

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ibrajix.multiclock.BuildConfig.DEBUG
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        else{
          Timber.plant(ReleaseTree())
        }

    }

}