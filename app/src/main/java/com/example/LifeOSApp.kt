package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.LifeOSRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LifeOSApp : Application() {

    lateinit var repository: LifeOSRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        val database = AppDatabase.getDatabase(this)
        repository = LifeOSRepository(database)

        CoroutineScope(Dispatchers.IO).launch {
            repository.checkAndSeedInitialData()
        }
    }

    companion object {
        lateinit var instance: LifeOSApp
            private set

        val repo: LifeOSRepository
            get() = instance.repository
    }
}
