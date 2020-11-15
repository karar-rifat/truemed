package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
class Settings
{
    constructor(title:String,description:String,status:Boolean,user_id:String){
        this.title = title
        this.description = description
        this.status = status
        this.user_id = user_id
    }

    //for testing
    @Ignore
    constructor(id:Int,title:String,description:String,status:Boolean,user_id:String){
        this.id = id
        this.title = title
        this.description = description
        this.status = status
        this.user_id = user_id
    }
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "description")
    var description: String? = null
    @ColumnInfo(name = "status")
    var status: Boolean? = null
    @ColumnInfo(name = "user_id")
    var user_id: String? = null
}