package com.smartearn.app.data.local

import androidx.room.*

@Dao
interface EarningsDao {
    @Query("SELECT * FROM earnings ORDER BY claimedAt DESC")
    suspend fun getAllEarnings(): List<EarningsEntity>

    @Query("SELECT SUM(amount) FROM earnings")
    suspend fun getTotalEarnings(): Double?

    @Query("SELECT SUM(amount) FROM earnings WHERE claimedAt >= :since")
    suspend fun getEarningsSince(since: Long): Double?

    @Insert
    suspend fun insertEarning(earning: EarningsEntity)
}