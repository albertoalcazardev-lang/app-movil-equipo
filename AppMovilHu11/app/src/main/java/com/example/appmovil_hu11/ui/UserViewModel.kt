package com.example.appmovil_hu11.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovil_hu11.data.ApiService
import com.example.appmovil_hu11.data.User
import kotlinx.coroutines.launch

sealed interface UserUiState {
    object Loading : UserUiState
    data class Success(val users: List<User>) : UserUiState
    object Error : UserUiState
}

class UserViewModel : ViewModel() {
    var uiState: UserUiState by mutableStateOf(UserUiState.Loading)
        private set

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val result = ApiService.instance.getUsers()
                UserUiState.Success(result)
            } catch (e: Exception) {
                UserUiState.Error
            }
        }
    }
}