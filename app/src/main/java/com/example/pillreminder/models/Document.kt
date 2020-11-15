package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "document")
class Document
//    (val id : Int, val title : String)
{

    constructor(title:String,uri:String?,file:String?,type:String,userId: String,
                updatedAt:String,synced:Boolean,createdBy:String,updatedBy:String){
//        this.id = id
        this.title = title
        this.uri = uri
        this.file = file
        this.type = type
        this.userId = userId
        this.updatedAt = updatedAt
        this.synced = synced
        this.createdBy = createdBy
        this.updatedBy = updatedBy
    }

    //for testing
    @Ignore
    constructor(localId:Long,title:String,uri:String?,file:String?,type:String,user_id: String,updated_at:String,synced:Boolean,createdBy:String,updatedBy:String){
        this.localId = localId
        this.title = title
        this.uri = uri
        this.file = file
        this.type = type
        this.userId = user_id
        this.updatedAt = updated_at
        this.synced = synced
        this.createdBy = createdBy
        this.updatedBy = updatedBy
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Long = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "uri")
    var uri: String? = null
    @ColumnInfo(name = "file")
    var file: String? = null
    @ColumnInfo(name = "type")
    var type: String? = null
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null

    var createdBy: String? = null
    var updatedBy: String? = null
}