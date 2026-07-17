package com.smartearn.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_details")
data class BankDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountNumber: String,
    val accountName: String,
    val bank: String,
    val createdAt: Long = System.currentTimeMillis()
)