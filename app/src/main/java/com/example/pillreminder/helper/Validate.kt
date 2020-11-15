package com.example.pillreminder.helper

import java.util.regex.Pattern

class Validate {

    private val patternNumber: Pattern = Pattern.compile("-?\\d+(\\.\\d+)?")
    private val patternPhone: Pattern = Pattern.compile("[0-9*#+() -]*");


    fun isNumeric(strNum: String?): Boolean {
        return if (strNum == null) {
            false
        }
        else patternNumber.matcher(strNum).matches()
    }
    fun invalidNumber(strNum: String?): Boolean {
        return if (strNum == null) {
            false
        }
        else patternPhone.matcher(strNum).matches()
    }

     fun validatePhoneNumber(phoneNo: String): Boolean {
        when {
            //validate phone numbers of format "1234567890"
            phoneNo.matches(Regex("\\d{11}")) -> return true
            //validate phone numbers of format "+1234567890"
            phoneNo.matches(Regex("\\+\\d{13}")) -> return true
            else -> return false
        }
    }

    fun isInt(str: String?) = str?.toIntOrNull()?.let { true } ?: false
}