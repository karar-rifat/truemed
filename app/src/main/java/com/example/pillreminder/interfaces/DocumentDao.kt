package com.example.pillreminder.interfaces

import androidx.room.*
import com.example.pillreminder.models.Document
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Report

@Dao
interface DocumentDao {
    @Insert
    fun saveDocument(doc : Document) : Long

    @Query("select * from `document` ORDER BY local_id DESC")
    fun documents() : List<Document>

    @Query("DELETE  from `document`" )
    fun clearDocument()

    @Query("SELECT * FROM `document` WHERE local_id =:id")
    fun getDocument(id: Int): Document

    @Query("select * from document WHERE user_id =:user_id AND synced=:isSynced")
    fun unSyncedDocuments(user_id: String,isSynced:Boolean=false): List<Document>

    @Query("select * from `document` WHERE user_id =:user_id AND title=:title")
    fun getDocumentDetails(title:String,user_id: String) : Document

    @Update
    fun update(u: Document)

    @Delete
    fun delete(u: Document)
}