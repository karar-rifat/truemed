package com.example.pillreminder.models

class DashboardData{
    constructor(id:Int,title:String,type:String,time:String){
        this.id = id
        this.title = title
        this.type = type
        this.time = time
    }

    constructor(id:Int,title:String,type:String,code:String,time:String,beforeOrAfter:String,dose:Int,stock:Int){
        this.id = id
        this.title = title
        this.type = type
        this.code = code
        this.time = time
        this.beforeOrAfter = beforeOrAfter
        this.dose = dose
        this.stock = stock
    }

    var id : Int = 0
    var title: String? = null
    var type: String? = null
    var code: String? = null
    var time: String? = null
    var beforeOrAfter: String? = null
    var dose: Int? = null
    var stock: Int? = null

}