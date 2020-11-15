package com.example.pillreminder.network.request

class ResetPasswordRequest(email:String,password:String,uuid:String) {
    var email:String?=email
    var password:String?=password
    var uuid:String?=uuid
}