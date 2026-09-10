package com.example.appmovil_hu11.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val phone: String,
    val name: UserName
)

data class UserName(
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String
) {
    val fullName: String
        get() = "${firstname.replaceFirstChar { it.uppercase() }} ${lastname.replaceFirstChar { it.uppercase() }}"

    val initials: String
        get() = "${firstname.take(1).uppercase()}${lastname.take(1).uppercase()}"
}