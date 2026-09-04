package com.risket.app

import android.app.Application
import com.risket.app.data.RisketDatabase
import com.risket.app.data.RisketRepository

class RisketApp : Application() {
    lateinit var repository: RisketRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = RisketDatabase.getInstance(this)
        repository = RisketRepository(db.dao())
    }
}
