package com.example.pillreminder.models

class PillAlarms{

    constructor(local_id:Int,remote_id:Int?,id_to_match:Int?,user_id:String,type:String,time:String,start_from:String,no_of_day:Int,task_id:Int,reminder:Boolean
                ,dose:Int,frequency:Int,before_or_after_meal:String,lowest_stock:Int,stock_reminder:Boolean){
        this.local_id = local_id
        this.remote_id = remote_id
        this.id_to_match = id_to_match
        this.user_id = user_id
        this.type = type //vaccine,appointment
        this.time = time
        this.start_from = start_from
        this.no_of_day = no_of_day
        this.task_id = task_id
        this.reminder=reminder
        this.dose=dose
        this.frequency=frequency
        this.before_or_after_meal=before_or_after_meal
        this.lowest_stock=lowest_stock
        this.stock_reminder=stock_reminder
    }

    var local_id : Int? = 0
    var remote_id : Int?=0
    var id_to_match: Int? = 0
    var user_id: String? = null
    var title: String? = null
    var type: String? = null
    var time: String? = null
    var start_from: String? = null
    var no_of_day: Int? = null
    var task_id: Int? = null
    var stock: Int? = 0
    var reminder: Boolean? = null
    var dose: Int? = null
    var frequency: Int? = null
    var before_or_after_meal: String? = null
    var updated_at: String? = null
    var synced: Boolean? = null
    var lowest_stock: Int? = 0
    var stock_reminder: Boolean? = false
}