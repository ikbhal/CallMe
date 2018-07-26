package com.muhammad.iqbal.callme

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(ContactData::class), version = 1)
abstract class ContactDataBase : RoomDatabase() {

    abstract fun contactDataDao(): ContactDataDao

    companion object {
        private var INSTANCE: ContactDataBase? = null

        fun getInstance(context: Context): ContactDataBase? {
            if (INSTANCE == null) {
                synchronized(ContactDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ContactDataBase::class.java, "ContactData.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
