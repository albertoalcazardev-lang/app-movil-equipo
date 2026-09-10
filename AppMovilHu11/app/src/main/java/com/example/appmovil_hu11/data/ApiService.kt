package com.example.appmovil_hu11.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("carts")
    suspend fun getCarts(): List<Cart>

    @GET("products")
    suspend fun getProducts(): List<ProductSimple>

    companion object {
        private const val BASE_URL = "https://fakestoreapi.com/"

        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}