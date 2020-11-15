package com.example.pillreminder.helper

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

public class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}