package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
class User{
//    constructor(name:String,email:String,password:String,dob:String){
//        this.name = name
//        this.email = email
//        this.password = password
//        this.dob = dob
//    }


    constructor(name:String,email:String,password:String,phoneNumber:String?,dateOfBirth:String?,gender:String?,
                height:String?,weight:String?,bloodGroup:String?,bloodPressure:String?,photo:String?,updatedAt:String,synced:Boolean){
        this.name = name
        this.email = email
        this.password = password
        this.phoneNumber = phoneNumber
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.height = height
        this.weight = weight
        this.bloodGroup = bloodGroup
        this.bloodPressure = bloodPressure
        this.photo = photo
        this.updatedAt = updatedAt
        this.synced = synced
    }

//    constructor(name:String,email:String,dob:String,gender:String,height:String,weight:String,bloodGroup: String,bp:String,photo:String,updatedAt:String,synced:Boolean){
//        this.name = name
//        this.email = email
//        this.dob = dob
//        this.gender = gender
//        this.height = height
//        this.weight = weight
//        this.bloodGroup = bloodGroup
//        this.bp = bp
//        this.photo = photo
//        this.updatedAt = updatedAt
//        this.synced = synced
//    }

    //for testing
    @Ignore
    constructor(localId:Int,name:String,email:String,password:String,phoneNumber:String?,dateOfBirth:String?,gender:String?,
                height:String?,weight:String?,bloodGroup:String?,bloodPressure:String?,photo:String?,updatedAt:String,synced:Boolean){
        this.localId = localId
        this.name = name
        this.email = email
        this.password = password
        this.phoneNumber = phoneNumber
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.height = height
        this.weight = weight
        this.bloodGroup = bloodGroup
        this.bloodPressure = bloodPressure
        this.photo = photo
        this.updatedAt = updatedAt
        this.synced = synced
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "email")
    @SerializedName("uid")
    var email: String? = null
    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = null
    @ColumnInfo(name = "password")
    var password: String? = null
    @ColumnInfo(name = "date_of_birth")
    var dateOfBirth: String? = null
    @ColumnInfo(name = "gender")
    var gender: String? = null
    @ColumnInfo(name = "height")
    var height: String? = null
    @ColumnInfo(name = "weight")
    var weight: String? = null
    @ColumnInfo(name = "blood_group")
    var bloodGroup: String? = null
    @ColumnInfo(name = "blood_pressure")
    var bloodPressure: String? = null
    @ColumnInfo(name = "photo")
    var photo: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null
}