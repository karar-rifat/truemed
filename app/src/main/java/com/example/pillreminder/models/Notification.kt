package com.example.pillreminder.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "notification")
class Notification {

    constructor(userId:String,title:String,type:String,date:String,time:String){
        this.title = title
        this.userId = userId
        this.type = type
        this.date = date
        this.time = time
    }


    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "type")
    var type: String? = null
    @ColumnInfo(name = "date")
    var date: String? = null
    @ColumnInfo(name = "time")
    var time: String? = null
    @ColumnInfo(name = "status")
    var status: String? = null

}