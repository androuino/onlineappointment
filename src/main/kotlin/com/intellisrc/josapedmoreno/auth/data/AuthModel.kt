package com.intellisrc.josapedmoreno.auth.data

data class AuthModel(var user: String = "", var pass: String = "") {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "user" to user,
            "pass" to pass
        )
    }
}