package com.example.quickresponsecode.data.enums

enum class Method {
    Generate,
    Scan,

    ;

    companion object {
        fun valueOfOrDefault(value: String): Method {
            return try {
                valueOf(value)
            } catch (_: Exception) {
                Generate
            }
        }
    }
}