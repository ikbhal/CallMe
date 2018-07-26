package com.muhammad.iqbal.callme

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface ContactDataDao {

    @Query("SELECT * from contacts")
    fun getAll(): List<ContactData>

    @Insert(onConflict = REPLACE)
    fun insert(contactData: ContactData)

    @Update
    fun update(contactData: ContactData)

    @Query("DELETE from contacts")
    fun deleteAll()
}