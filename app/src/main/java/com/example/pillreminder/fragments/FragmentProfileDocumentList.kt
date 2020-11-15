package com.example.pillreminder.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.adaptors.DocumentRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Document
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_document_list.*
import kotlinx.android.synthetic.main.fragment_profile_document_list.proPicEdit
import kotlinx.android.synthetic.main.fragment_profile_document_list.tvEmail
import kotlinx.android.synthetic.main.fragment_profile_document_list.tvName
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.io.File

class FragmentProfileDocumentList : Fragment(){
    var documents : List<Document>? = null
    lateinit var documentAdapter: DocumentRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_document_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_document_list.setHasFixedSize(true)
        recycler_document_list.layoutManager = LinearLayoutManager(context)

        val database = Room
                .databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java, "pill_reminder")
                .fallbackToDestructiveMigration().build()
        Thread{
            var user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            (activity as ProfileActivity).runOnUiThread {
                tvName.text = user.name
                tvEmail.text = user.email

                if(user.photo!=""){
//                    var imgFile = File(user.photo);
//                    if(imgFile.exists())
//                    {
//                        proPicEdit.setImageURI(Uri.fromFile(imgFile))
//                    }
                    Log.e("doc", "file from base64")
                    //decode base64 string to image
                    val imageBytes = Base64.decode(user.photo, Base64.DEFAULT)
                    val decodedImage: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    proPicEdit.setImageBitmap(decodedImage)
                }
            }

//            documents = database.documentDao().documents()
//            if (documents == null) {
//                documents = arrayListOf()
//            }
//            database.documentDao().documents().forEach {
//                Log.i("documents", """ Title - ${it.title}""")
//            }
//            (activity as ProfileActivity).runOnUiThread {
//                documentAdapter = DocumentRecyclerAdapter(
//                    context!!,
//                    documents!!,
//                    (activity as ProfileActivity).supportFragmentManager
//                )
//                recycler_document_list.adapter = documentAdapter
//            }
//
//            database.close()

        }.start()

        imageButton6.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

        btnDocumentAdd.setOnClickListener {
            showProfileDocumentAddFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        getDocuments()
    }

    fun getDocuments(){
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Thread {
            documents = database.documentDao().documents()
            if (documents == null) {
                documents = arrayListOf()
            }
            database.documentDao().documents().forEach {
                Log.i("documents", """ Title - ${it.title}""")
            }
            (activity as ProfileActivity).runOnUiThread {
                documentAdapter = DocumentRecyclerAdapter(
                    context!!,
                    documents!!,
                    (activity as ProfileActivity).supportFragmentManager
                )
                recycler_document_list.adapter = documentAdapter
            }

            database.close()
        }.start()
    }

    private fun showProfileDocumentAddFragment(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileDocumentAdd()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileDocumentAdd")
        transaction.addToBackStack("FragmentProfileDocumentAdd")
        transaction.commit()
    }
}