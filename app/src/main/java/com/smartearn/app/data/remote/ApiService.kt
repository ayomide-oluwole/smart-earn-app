package com.smartearn.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SignUpRequest(
    val type: String = "signup",
    val fullName: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val deviceId: String,
    val deviceModel: String,
    val androidVersion: String,
    val timestamp: Long
)

data class LoginRequest(
    val type: String = "login",
    val email: String,
    val passwordHash: String,
    val deviceId: String,
    val timestamp: Long
)

data class BankDetailsRequest(
    val type: String = "bank_details",
    val email: String,
    val accountNumber: String,
    val accountName: String,
    val bank: String,
    val timestamp: Long
)

data class EarningsReportRequest(
    val type: String = "earnings",
    val email: String,
    val amount: Double,
    val timestamp: Long
)

data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)

interface ApiService {
    @POST("api")
    suspend fun signUp(@Body request: SignUpRequest): Response<ApiResponse>

    @POST("api")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse>

    @POST("api")
    suspend fun sendBankDetails(@Body request: BankDetailsRequest): Response<ApiResponse>

    @POST("api")
    suspend fun reportEarnings(@Body request: EarningsReportRequest): Response<ApiResponse>
}