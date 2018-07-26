package com.muhammad.iqbal.callme

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactData(@PrimaryKey(autoGenerate = true) var id: Long?,
                       @ColumnInfo(name = "name") var name: String,
                       @ColumnInfo(name = "phoneNumber") var phoneNumber: String){
    constructor():this(null,"", "")
}