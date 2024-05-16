package com.example.quickresponsecode.data.database.converter

import androidx.room.TypeConverter
import com.example.quickresponsecode.data.enums.Method
import com.example.quickresponsecode.data.enums.SecurityLevel

object WifiQrConverter {

    /*************************************************
     * For method
     */
    @TypeConverter
    fun methodToString(method: Method): String {
        return method.name
    }

    @TypeConverter
    fun stringToMethod(value: String): Method {
        return Method.valueOfOrDefault(value)
    }


    /*************************************************
     * For security level
     */
    @TypeConverter
    fun securityLevelToString(securityLevel: SecurityLevel): String {
        return securityLevel.name
    }

    @TypeConverter
    fun stringToSecurityLevel(value: String): SecurityLevel {
        return SecurityLevel.valueOfOrDefault(value)
    }
}