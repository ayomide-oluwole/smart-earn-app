package com.smartearn.app.util

object EarningsCalculator {
    // ₦500 per 24 hours = ₦125 per 6-hour cycle
    private const val TOTAL_DAILY_EARNINGS = 500.0
    private const val CYCLE_HOURS = 6
    private const val CYCLES_PER_DAY = 24 / CYCLE_HOURS
    const val CYCLE_MILLIS = CYCLE_HOURS * 60 * 60 * 1000L
    const val CLAIM_AMOUNT = TOTAL_DAILY_EARNINGS / CYCLES_PER_DAY  // ₦125

    fun getRatePerSecond(): Double {
        return TOTAL_DAILY_EARNINGS / (24 * 60 * 60)
    }

    fun calculatePendingEarnings(lastClaimTime: Long): Double {
        val elapsed = System.currentTimeMillis() - lastClaimTime
        val cappedElapsed = minOf(elapsed, CYCLE_MILLIS)
        val earnings = (cappedElapsed / 1000.0) * getRatePerSecond()
        return Math.round(earnings * 100.0) / 100.0
    }

    fun getTimeUntilNextClaim(lastClaimTime: Long): Long {
        val elapsed = System.currentTimeMillis() - lastClaimTime
        return maxOf(0, CYCLE_MILLIS - elapsed)
    }

    fun canClaim(lastClaimTime: Long): Boolean {
        return (System.currentTimeMillis() - lastClaimTime) >= CYCLE_MILLIS
    }
}