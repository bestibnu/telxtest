package com.telxtest.android.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/request-otp")
    suspend fun requestOtp(@Body request: OtpRequest): AuthRequestResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): AuthVerifyResponse

    @POST("calls")
    suspend fun placeCall(@Body request: CallRequest): CallRecord

    @GET("credits")
    suspend fun fetchCredits(): CreditBalance

    @POST("credits/topup")
    suspend fun topUp(@Body request: TopUpRequest): CreditBalance
}
