package com.shubham.a7minutesworkout

import android.app.Application

class HistoryApplication: Application() {

    val db by  lazy {
        HistoryDatabase.getInstance(this)
    }
}