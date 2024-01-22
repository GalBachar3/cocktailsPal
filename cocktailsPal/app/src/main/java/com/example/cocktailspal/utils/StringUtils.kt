package com.example.cocktailspal.utils

object StringUtils {
    fun isBlank(str: String?): Boolean {
        return if (str != null && !str.isEmpty() && !str.trim { it <= ' ' }.isEmpty()) {
            true
        } else false
    }
}
