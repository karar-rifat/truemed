package com.example.pillreminder.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "report")
class Report {

    constructor(userId:String,name:String,type:String,date:Date,time:String,scheduleTime:String?,status:String,updatedAt:String,synced:Boolean){
        this.name = name
        this.userId = userId
        this.type = type
        this.date = date
        this.time = time
        this.scheduleTime = scheduleTime
        this.status = status
        this.updatedAt = updatedAt
        this.synced = synced
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "type")
    var type: String? = null
    @ColumnInfo(name = "date")
    var date: Date? = null
    @ColumnInfo(name = "time")
    var time: String? = null
    @ColumnInfo(name = "schedule_time")
    var scheduleTime: String? = null
    @ColumnInfo(name = "status")
    var status: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null

}