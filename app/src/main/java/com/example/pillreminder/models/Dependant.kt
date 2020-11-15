package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "dependant")
class Dependant{
    constructor(user_id:String,name:String,gender:String,dob:String,relationship:String,dependant_email: String){
        this.user_id = user_id
        this.name = name
        this.gender = gender
        this.dob = dob
        this.relationship = relationship
        this.dependant_email = dependant_email
    }

    //for testing
    @Ignore
    constructor(id:Long,user_id:String,name:String,gender:String,dob:String,relationship:String,dependant_email: String){
        this.id = id
        this.user_id = user_id
        this.name = name
        this.gender = gender
        this.dob = dob
        this.relationship = relationship
        this.dependant_email = dependant_email
    }

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "gender")
    var gender: String? = null
    @ColumnInfo(name = "dob")
    var dob: String? = null
    @ColumnInfo(name = "relationship")
    var relationship: String? = null
    @ColumnInfo(name = "dependant_email")
    var dependant_email: String? = null
    @ColumnInfo(name = "user_id")
    var user_id: String? = null
}