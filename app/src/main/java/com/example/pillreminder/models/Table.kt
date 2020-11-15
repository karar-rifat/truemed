package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table")
class Table{

    constructor(title:String,userId:String,updatedAt:String,synced:Boolean){
//        this.id = id
        this.title = title
        this.userId = userId
        this.updatedAt = updatedAt
        this.synced = synced
    }

    //for testing
    @Ignore
    constructor(localId:Int,title:String,userId:String,updatedAt:String,synced:Boolean){
        this.localId = localId
        this.title = title
        this.userId = userId
        this.updatedAt = updatedAt
        this.synced = synced
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null
}