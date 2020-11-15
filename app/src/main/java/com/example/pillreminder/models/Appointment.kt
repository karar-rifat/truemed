package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "appointment")
class Appointment
//    (val id : Int, val title : String)
{

    constructor(title:String,place:String,mobile:String,time:String,reminder:Boolean,note:String,
                taskId:Long,userId:String,updatedAt:String,synced:Boolean){
//        this.id = id
        this.title = title
        this.place = place
        this.mobile = mobile
        this.time = time
        this.reminder = reminder
        this.note = note
        this.taskId = taskId
        this.userId = userId
        this.updatedAt = updatedAt
        this.synced = synced
    }

    //for testing
    @Ignore
    constructor(localId:Int,remoteId:Int,title:String,place:String,mobile:String,time:String,reminder:Boolean,note:String,
                taskId:Long,userId:String,updatedAt:String,synced:Boolean){
        this.localId = localId
        this.remoteId = remoteId
        this.title = title
        this.place = place
        this.mobile = mobile
        this.time = time
        this.reminder = reminder
        this.note = note
        this.taskId = taskId
        this.userId = userId
        this.updatedAt = updatedAt
        this.synced = synced
    }

//
//    constructor(){
//    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "place")
    var place: String? = null
    @ColumnInfo(name = "mobile")
    var mobile: String? = null
    @ColumnInfo(name = "time")
    var time: String? = null
    @ColumnInfo(name = "reminder")
    var reminder: Boolean? = null
    @ColumnInfo(name = "note")
    var note: String? = null
    @ColumnInfo(name = "task_id")
    var taskId: Long? = null
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null
}