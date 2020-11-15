package com.example.pillreminder.models


class AppointmentVaccineAlarms {

    constructor(local_id:Int?,remote_id:Int?,id_to_match:Int?,title:String,place:String,mobile:String,time:String,reminder:Boolean,task_id:Long,user_id:String,type:String){
        this.local_id = local_id
        this.remote_id = remote_id
        this.id_to_match = id_to_match
        this.title = title
        this.place = place
        this.mobile = mobile
        this.time = time
        this.reminder = reminder
        this.type = type
        this.task_id = task_id
        this.user_id = user_id
//        this.updatedAt = updatedAt
//        this.synced = synced
    }

    var local_id : Int? = 0
    var remote_id : Int?=0
    var id_to_match: Int? = 0
    var title: String? = null
    var place: String? = null
    var mobile: String? = null
    var time: String? = null
    var reminder: Boolean? = null
    var type: String? = null
    var task_id: Long? = null
    var user_id: String? = null
    var updated_at: String? = null
    var synced: Boolean? = null
}
