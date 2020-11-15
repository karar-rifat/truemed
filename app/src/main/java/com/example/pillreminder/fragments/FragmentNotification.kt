package com.example.pillreminder.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.adaptors.InformationRecyclerAdapter
import com.example.pillreminder.adaptors.NotificationRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.models.Information
import com.example.pillreminder.models.Notification
import com.example.pillreminder.network.repository.InformationRepo
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_information_list.*
import kotlinx.android.synthetic.main.fragment_notification_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FragmentNotification : Fragment() {

    lateinit var notificationRecyclerAdapter: NotificationRecyclerAdapter
    var notifications: ArrayList<Notification>? = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_notification_list.setHasFixedSize(true)
        recycler_notification_list.layoutManager = LinearLayoutManager(context)

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        )
            .fallbackToDestructiveMigration().build()

        GlobalScope.launch(Dispatchers.IO) {
            notifications = database.NotificationDao().notifications() as ArrayList<Notification>?

            updateData()

            database.close()
        }

        btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

    }

    override fun onResume() {
        super.onResume()
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        )
            .fallbackToDestructiveMigration().build()

        GlobalScope.launch(Dispatchers.IO) {
            notifications = database.NotificationDao().notifications() as ArrayList<Notification>?

            updateData()

            database.close()
        }
    }

    fun updateData(){

        activity?.runOnUiThread {
            notificationRecyclerAdapter = NotificationRecyclerAdapter(
                context!!,
                notifications!!,
                activity!!.supportFragmentManager
            )
            recycler_notification_list.adapter = notificationRecyclerAdapter
        }
    }
}