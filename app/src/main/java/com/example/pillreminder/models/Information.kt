package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "information")
class Information
{

    constructor(remoteId:Int?,title:String?,description:String?,imageFile:String?,language:String?,category: String?){
        this.remoteId = remoteId
        this.title = title
        this.description = description
        this.imageFile = imageFile
        this.language = language
        this.category = category
    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "description")
    var description: String? = null
    @ColumnInfo(name = "image")
    var imageFile: String? = null
    @ColumnInfo(name = "language")
    var language: String? = null
    @ColumnInfo(name = "category")
    var category: String? = null
}