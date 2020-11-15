package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
class UserDetails {
    constructor(name:String,email:String,password:String,dateOfBirth:String){
        this.name = name
        this.email = email
        this.password = password
        this.dateOfBirth = dateOfBirth
    }

    //for testing
    @Ignore
    constructor(id:Int,name:String,email:String,password:String,dateOfBirth:String){
        this.id = id
        this.name = name
        this.email = email
        this.password = password
        this.dateOfBirth = dateOfBirth
    }
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    @ColumnInfo(name = "user_d")
    var userId: String? = null
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "email")
    var email: String? = null
    @ColumnInfo(name = "password")
    var password: String? = null
    @ColumnInfo(name = "date_of_birth")
    var dateOfBirth: String? = null
}