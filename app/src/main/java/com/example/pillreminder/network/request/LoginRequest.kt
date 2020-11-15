package com.example.pillreminder.network.request

class LoginRequest(email:String,password:String) {
    var uid:String?=email
    var password:String?=password
}