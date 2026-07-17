package com.smartearn.app.data.local

import androidx.room.*

@Dao
interface BankDetailsDao {
    @Query("SELECT * FROM bank_details ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestBankDetails(): BankDetailsEntity?

    @Insert
    suspend fun insertBankDetails(details: BankDetailsEntity)
}