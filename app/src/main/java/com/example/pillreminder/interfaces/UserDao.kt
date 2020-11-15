package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.User

@Dao
interface UserDao {
    @Insert
    fun saveUser(table : User) : Long

//    @Query("select * from `user`" )
//    fun users() : List<User>

    @Query("DELETE  from `user`" )
    fun clearUser()

    @Query("select * from `user` WHERE email=:user_id AND synced=:isSynced" )
    fun unSyncedUser(user_id:String, isSynced:Boolean=false) : List<User>

    @Query("SELECT * FROM `user` WHERE local_id =:id")
    fun getUser(id: Int): User

    @Query("SELECT * FROM `user` WHERE email =:email" )
    fun getUserByEmail(email: String): User

    @Query("SELECT * FROM `user` WHERE `email` =:email AND `password` =:password" )
    fun checkUser(email: String, password: String): User

    @Query("SELECT * FROM `user` WHERE `email` =:email" )
    fun checkIfExist(email: String): List<User>

    @Update
    fun update(u: User)

    @Delete
    fun delete(u: User)
}