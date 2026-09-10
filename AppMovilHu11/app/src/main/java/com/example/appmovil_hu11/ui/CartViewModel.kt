package com.example.appmovil_hu11.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovil_hu11.data.ApiService
import com.example.appmovil_hu11.data.CartItemUI
import com.example.appmovil_hu11.data.CartUI
import kotlinx.coroutines.launch

sealed interface CartUiState {
    object Loading : CartUiState
    data class Success(val carts: List<CartUI>) : CartUiState
    object Error : CartUiState
}

class CartViewModel : ViewModel() {
    var uiState: CartUiState by mutableStateOf(CartUiState.Loading)
        private set

    init {
        fetchCartsAndProducts()
    }

    fun fetchCartsAndProducts() {
        viewModelScope.launch {
            uiState = CartUiState.Loading
            uiState = try {
                val cartsRaw = ApiService.instance.getCarts()
                val productsList = ApiService.instance.getProducts()

                val productMap = productsList.associateBy({ it.id }, { it.title })

                val cartsProcessed = cartsRaw.map { cart ->
                    CartUI(
                        id = cart.id,
                        userId = cart.userId,
                        date = cart.date.split("T").firstOrNull() ?: cart.date,
                        items = cart.products.map { item ->
                            CartItemUI(
                                productId = item.productId,
                                productTitle = productMap[item.productId] ?: "Producto #${item.productId}",
                                quantity = item.quantity
                            )
                        }
                    )
                }

                CartUiState.Success(cartsProcessed)
            } catch (e: Exception) {
                CartUiState.Error
            }
        }
    }
}