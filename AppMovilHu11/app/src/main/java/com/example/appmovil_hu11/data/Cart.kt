package com.example.appmovil_hu11.data

import com.google.gson.annotations.SerializedName


data class Cart(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductItem>
)


data class CartProductItem(
    @SerializedName("productId") val productId: Int,
    val quantity: Int
)


data class ProductSimple(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String
)


data class CartItemUI(
    val productId: Int,
    val productTitle: String,
    val quantity: Int
)

data class CartUI(
    val id: Int,
    val userId: Int,
    val date: String,
    val items: List<CartItemUI>
)