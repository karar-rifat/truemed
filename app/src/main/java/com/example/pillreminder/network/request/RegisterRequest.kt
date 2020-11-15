package com.example.pillreminder.network.request

class RegisterRequest {


    constructor(uid: String,name: String,email: String,password:String,type: String) {
        this.uid=uid
        this.name=name
        this.email=email
        this.password=password
        this.type=type
    }

    constructor(uid: String, name: String, email: String, password:String, phoneNumber: String?, type: String){
        this.uid=uid
        this.name=name
        this.email=email
        this.password=password
        this.phoneNumber=phoneNumber
        this.type=type
    }

    var uid:String?=null
    var name:String?=null
    var email:String?=null
    var password:String?=null
    var phoneNumber:String?=null
    var type:String?=null

}