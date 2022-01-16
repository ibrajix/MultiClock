package com.ibrajix.multiclock.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@SmallTest
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var database: Database
    private lateinit var alarmDao: AlarmDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
            .allowMainThreadQueries()
            .build()
        alarmDao = database.alarmDao()
    }

    @After
    fun closeDb(){
        database.close()
    }


    @Test
    fun insertAlarmAndCheckIfInserted() = runTest {

        val alarm = Alarm(
            id = 1,
            status = true,
            time = "1234",
            vibrate = true
        )

        alarmDao.insertAlarm(alarm)
        val getAllAlarms = alarmDao.getAllAlarms().first()
        assertThat(getAllAlarms).contains(alarm)

    }

    @Test
    fun updateAlarmAndCheckIfUpdated() = runTest {

        val alarm = Alarm(
            id = 2,
            status = false,
            time = "12344",
            vibrate = true
        )

        alarmDao.insertAlarm(alarm)

        val newAlarm = Alarm(
            id = 2,
            status = true,
            time = "1344455",
            vibrate = false
        )

        alarmDao.updateAlarm(newAlarm)

        val getAllAlarms = alarmDao.getAllAlarms().first()

        assertThat(getAllAlarms).contains(newAlarm)

    }

    @Test
    fun deleteAlarmAndCheckIfDeleted() = runTest {

        val alarm = Alarm(
            id = 1,
            status = true,
            time = "1234",
            vibrate = false
        )

        alarmDao.insertAlarm(alarm)

        alarmDao.deleteAlarm(1)

        val getAllAlarms = alarmDao.getAllAlarms().first()

        assertThat(getAllAlarms).doesNotContain(alarm)

    }

}