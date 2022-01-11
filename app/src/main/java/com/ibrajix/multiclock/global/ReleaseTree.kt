package com.ibrajix.multiclock.global

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.annotations.NotNull
import timber.log.Timber

class ReleaseTree : @NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        try {
            if (t != null) {
                if (tag != null) {
                    crashlytics.setCustomKey("multiclock_crash_tag", tag)
                }
                crashlytics.log(t.toString())
            } else {
                crashlytics.log(priority.toString() + tag + message)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}