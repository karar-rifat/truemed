package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.AppointmentVaccineAlarms
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Vaccine

@Dao
interface VaccineDao {
    @Insert
    fun saveVaccine(vaccine : Vaccine) : Long

    @Query("select * from `vaccine`")
    fun vaccines() : List<Vaccine>

    @Query("DELETE  from `vaccine`" )
    fun clearVaccine()

    @Query("select * from `vaccine` WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedVaccines(user_id: String,isSynced:Boolean=false) : List<Vaccine>

    @Query("SELECT * FROM `vaccine` WHERE local_id =:id")
    fun getVaccine(id: Int): Vaccine

    @Query("select * from `vaccine` WHERE user_id =:user_id AND title=:title")
    fun getVaccineDetails(title:String,user_id: String) : Vaccine

//    @Query("SELECT * FROM `vaccine`  WHERE `time` =:time AND reminder=:reminder")
//    fun getActiveVaccine(time: String,reminder:Boolean): List<AppointmentVaccineAlarms>

    @Query("SELECT time_pivot.*,vaccine.reminder AS reminder,vaccine.place AS place,vaccine.mobile AS mobile  FROM `time_pivot` JOIN vaccine ON time_pivot.title=vaccine.title  WHERE time_pivot.time =:time AND time_pivot.type =:type AND vaccine.reminder=:reminder")
    fun getActiveVaccine(time: String, type: String,reminder:Boolean): List<AppointmentVaccineAlarms>


    @Query("SELECT * FROM `vaccine` WHERE user_id =:user_id")
    fun getUserVaccines(user_id: String): List<Vaccine>

    @Update
    fun update(u: Vaccine)

    @Delete
    fun delete(u: Vaccine)

//    @Query("SELECT * FROM vaccine WHERE time LIKE :search")
@Query("SELECT vaccine.*,time_pivot.time AS time FROM vaccine JOIN time_pivot ON vaccine.title=time_pivot.title WHERE time_pivot.type = :type AND time_pivot.time LIKE :search")
fun dateWiseVaccines(type:String,search: String?): List<Vaccine>
//    search = "%fido%";
}