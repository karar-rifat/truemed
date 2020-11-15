package com.example.pillreminder.network.response

class MedicineResponse {
    var id:Int? = null
    var productname:String? = null
    var genericname:String? = null
    var companyname:String? = null

    constructor(id:Int,name:String,generic:String,company:String){
        this.id=id
        this.productname=name
        this.genericname=generic
        this.companyname=company
    }
}