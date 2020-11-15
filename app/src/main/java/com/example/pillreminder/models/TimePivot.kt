package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "time_pivot")
class TimePivot{


    constructor(idToMatch:Int,userId:String,title:String,type:String,time:String,taskId:Long,updatedAt:String,synced:Boolean){
        this.idToMatch = idToMatch
        this.userId = userId
        this.title = title
        this.type = type //vaccine,appointment
        this.time = time
        this.taskId = taskId
        this.updatedAt = updatedAt
        this.synced = synced
    }

    @Ignore
    constructor(remoteId:Int,idToMatch:Int,userId:String,title:String,type:String,time:String,taskId:Long,updatedAt:String,synced:Boolean){
        this.remoteId = remoteId
        this.idToMatch = idToMatch
        this.userId = userId
        this.title = title
        this.type = type //vaccine,appointment
        this.time = time
        this.taskId = taskId
        this.updatedAt = updatedAt
        this.synced = synced
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "id_to_match")
    var idToMatch: Int = 0
    @ColumnInfo(name = "user_id")
    var userId: String?=null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "type")
    var type: String? = null
    @ColumnInfo(name = "time")
    var time: String? = null
    @ColumnInfo(name = "task_id")
    var taskId: Long? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null
}