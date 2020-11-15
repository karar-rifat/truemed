package com.example.pillreminder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pill")
class Pill
//    (val id : Int, val title : String)
{

    constructor(tableId:Int,title:String,morning:String,noon:String,evening:String,
                night:String,beforeOrAfterMeal:String,everyday:Boolean,frequency: Int,noOfDay:Int,
                noOfDose:Int,stock:Int,lastPillStatus:String,reminder:Boolean,startFrom:String,
                updatedAt:String,taskIdMorning:Long,taskIdNoon:Long,taskIdEvening:Long,
                taskIdNight:Long,userId:String,synced:Boolean,lowestStock:Int,stockReminder:Boolean){
//        this.id = id
        this.tableId = tableId
        this.title = title
        this.morning = morning
        this.noon = noon
        this.evening = evening
        this.night = night
        this.beforeOrAfterMeal = beforeOrAfterMeal
        this.everyday = everyday
        this.frequency = frequency
        this.noOfDay = noOfDay
        this.noOfDose = noOfDose
        this.stock = stock
        this.lastPillStatus = lastPillStatus
        this.reminder = reminder
        this.startFrom = startFrom
        this.updatedAt = updatedAt
        this.taskIdMorning = taskIdMorning
        this.taskIdNoon = taskIdNoon
        this.taskIdEvening = taskIdEvening
        this.taskIdNight = taskIdNight
        this.userId = userId
        this.synced = synced

        this.lowestStock=lowestStock
        this.stockReminder=stockReminder
    }

    //for testing
    @Ignore
    constructor(remoteId:Int?,tableId:Int?,title:String?,morning:String?,noon:String?,evening:String?,
                night:String?,beforeOrAfterMeal:String?,everyday:Boolean?,frequency: Int?,noOfDay:Int?,
                noOfDose:Int?,stock:Int?,lastPillStatus:String?,reminder:Boolean?,startFrom:String?,
                updatedAt:String?,taskIdMorning:Long?,taskIdNoon:Long?,taskIdEvening:Long?,
                taskIdNight:Long?,userId:String?,synced:Boolean?,lowestStock:Int?,stockReminder:Boolean?){
        this.remoteId = remoteId
        this.tableId = tableId
        this.title = title
        this.morning = morning
        this.noon = noon
        this.evening = evening
        this.night = night
        this.beforeOrAfterMeal = beforeOrAfterMeal
        this.everyday = everyday
        this.frequency = frequency
        this.noOfDay = noOfDay
        this.noOfDose = noOfDose
        this.stock = stock
        this.lastPillStatus = lastPillStatus
        this.reminder = reminder
        this.startFrom = startFrom
        this.updatedAt = updatedAt
        this.taskIdMorning = taskIdMorning
        this.taskIdNoon = taskIdNoon
        this.taskIdEvening = taskIdEvening
        this.taskIdNight = taskIdNight
        this.userId = userId
        this.synced = synced

        this.lowestStock=lowestStock
        this.stockReminder=stockReminder
    }

    //for testing
    @Ignore
    constructor(localId:Int,remoteId:Int?,tableId:Int?,title:String?,morning:String?,noon:String?,evening:String?,
                night:String?,beforeOrAfterMeal:String?,everyday:Boolean?,frequency: Int?,noOfDay:Int?,
                noOfDose:Int?,stock:Int?,lastPillStatus:String?,reminder:Boolean?,startFrom:String?,
                updatedAt:String?,taskIdMorning:Long?,taskIdNoon:Long?,taskIdEvening:Long?,
                taskIdNight:Long?,userId:String?,synced:Boolean?,lowestStock:Int?,stockReminder:Boolean?){
        this.localId = localId
        this.remoteId = remoteId
        this.tableId = tableId
        this.title = title
        this.morning = morning
        this.noon = noon
        this.evening = evening
        this.night = night
        this.beforeOrAfterMeal = beforeOrAfterMeal
        this.everyday = everyday
        this.frequency = frequency
        this.noOfDay = noOfDay
        this.noOfDose = noOfDose
        this.stock = stock
        this.lastPillStatus = lastPillStatus
        this.reminder = reminder
        this.startFrom = startFrom
        this.updatedAt = updatedAt
        this.taskIdMorning = taskIdMorning
        this.taskIdNoon = taskIdNoon
        this.taskIdEvening = taskIdEvening
        this.taskIdNight = taskIdNight
        this.userId = userId
        this.synced = synced

        this.lowestStock=lowestStock
        this.stockReminder=stockReminder
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    var localId : Int = 0
    @ColumnInfo(name = "remote_id")
    @SerializedName("id")
    var remoteId: Int? = null
    @ColumnInfo(name = "table_id")
    var tableId: Int? = 0
    @ColumnInfo(name = "title")
    var title: String? = null
    @ColumnInfo(name = "morning")
    var morning: String? = null
    @ColumnInfo(name = "noon")
    var noon: String? = null
    @ColumnInfo(name = "evening")
    var evening: String? = null
    @ColumnInfo(name = "night")
    var night: String? = null
    @ColumnInfo(name = "before_or_after_meal")
    var beforeOrAfterMeal: String? = null
    @ColumnInfo(name = "everyday")
    var everyday: Boolean? = null
    @ColumnInfo(name = "frequency")
    var frequency: Int? = null
    @ColumnInfo(name = "no_of_day")
    var noOfDay: Int? = null
    @ColumnInfo(name = "no_of_dose")
    var noOfDose: Int? = null
    @ColumnInfo(name = "stock")
    var stock: Int? = null
    @ColumnInfo(name = "task_id_morning")
    var taskIdMorning: Long? = null
    @ColumnInfo(name = "task_id_noon")
    var taskIdNoon: Long? = null
    @ColumnInfo(name = "task_id_evening")
    var taskIdEvening: Long? = null
    @ColumnInfo(name = "task_id_night")
    var taskIdNight: Long? = null
    @ColumnInfo(name = "reminder")
    var reminder: Boolean? = null
    @ColumnInfo(name = "last_pill_status")
    var lastPillStatus: String? = null
    @ColumnInfo(name = "start_from")
    var startFrom: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "user_id")
    var userId: String? = null
    @ColumnInfo(name = "synced")
    var synced: Boolean? = null

    @ColumnInfo(name = "lowest_stock")
    var lowestStock: Int? = 0
    @ColumnInfo(name = "stock_reminder")
    var stockReminder: Boolean? = true
}