/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() = runTest { // from https://developer.android.com/kotlin/coroutines/test#invoking-suspending-functions
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    fun insertAndGetSleepQuality() = runTest {
        val night = SleepNight(123123, 124124551, 123123123, 5)
        sleepDao.insert(night)
        val nightTest = sleepDao.get(123123)
        assertEquals(nightTest?.sleepQuality, 5)
    }

    @Test
    fun insertUpdateAndGetValues() = runTest {
        val night = SleepNight(2222, 124124551, 123123123, 3)
        sleepDao.insert(night)

        night.endTimeMilli = 8888888
        night.sleepQuality = 9
        sleepDao.update(night)

        val nightTest = sleepDao.get(2222)
        assertEquals(nightTest?.endTimeMilli, 8888888.toLong())
        assertEquals(nightTest?.sleepQuality, 9)
    }

// FIXME: issue is happening because LiveData will wait for an observer to observe it
// before populating its value. To fix this, a more complex test must be written
//
//    private val mMediatorLiveData: MediatorLiveData<List<SleepNight>> = MediatorLiveData()
//
//    @Test
//    fun insert5NightsAndListThem() {
//        val night = SleepNight(0,12314124,214214124,2)
//        val night2 = SleepNight(0,14242144124,21111114124,7)
//        val night3 = SleepNight(0,12314444444,21422242124,1)
//        val night4 = SleepNight(0,1231415555554,214266666124,6)
//        val night5 = SleepNight(0,123166666664,2142141777774,4)
//
//        val listNights = sleepDao.getAllNights()
//
//        runTest {
//            sleepDao.insert(night)
//            sleepDao.insert(night2)
//            sleepDao.insert(night3)
//            sleepDao.insert(night4)
//            sleepDao.insert(night5)
//        }
//
//        Log.d("Testing", "night=${night}")
//        Log.d("Testing", "night2=${night2}")
//        Log.d("Testing", "night3=${night3}")
//        Log.d("Testing", "night4=${night4}")
//        Log.d("Testing", "night5=${night5}")
//
//        mMediatorLiveData.addSource(listNights) {
//            fun onChanged(sleepList: List<SleepNight>?) {
//                if (sleepList.isNullOrEmpty()) {
//                    // Fetch data from API
//                } else {
//                    mMediatorLiveData.removeSource(listNights as LiveData<*>)
//                    mMediatorLiveData.setValue(sleepList)
//                }
//            }
//        }
//
//        Log.d("Testing", "listNights.value=${listNights.value}")
//
//        assertEquals(listNights.value?.size, 5)
//
////        val listNights = sleepDao.getAllNights().value?.size
////        assertEquals(listNights, 5)
//    }

}

