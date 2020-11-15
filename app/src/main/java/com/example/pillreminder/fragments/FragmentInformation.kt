package com.example.pillreminder.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import com.example.pillreminder.adaptors.AppointmentRecyclerAdapter
import com.example.pillreminder.adaptors.InformationRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.Information
import com.example.pillreminder.network.repository.InformationRepo
import com.example.pillreminder.network.repository.LoginRepo
import com.example.pillreminder.network.request.LoginRequest
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_appointment_list.*
import kotlinx.android.synthetic.main.fragment_information_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class FragmentInformation : Fragment() {

    lateinit var informationRecyclerAdapter: InformationRecyclerAdapter
    var informations: ArrayList<Information>? = arrayListOf()

    companion object{
        var category:String?="instruction"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        category=arguments?.getString("category")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_information_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_information_list.setHasFixedSize(true)
        recycler_information_list.layoutManager = LinearLayoutManager(context)

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        )
            .fallbackToDestructiveMigration().build()

        GlobalScope.launch(Dispatchers.IO) {
            informations?.clear()
            informations = database.informationDao().informations() as ArrayList<Information>?

            val token = SessionManager(AccountKitController.getApplicationContext()).fetchAuthToken()
            if (informations.isNullOrEmpty()) {
                informations = InformationRepo().getInformation(
                    token!!,
                    ::informationHandler
                ) as ArrayList<Information>
            } else {
                updateData()
            }
            database.close()
        }

    }

    override fun onResume() {
        super.onResume()

        updateData()

    }

    fun informationHandler(informationList: List<Information>) {
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        )
            .fallbackToDestructiveMigration().build()
        informations = informationList as ArrayList<Information>

        updateData()

        Log.e("Information handler", "size ${informations!!.size}")

        Thread {
            informationList.forEach {
                database.informationDao().saveInformation(it)
                Log.e("Information handler", "title ${it.title} cat ${it.category}")
            }
        }.start()
        database.close()

    }

    fun updateData(){
        val filteredInformations: ArrayList<Information>? = arrayListOf()
        filteredInformations?.clear()
        informations?.forEach {
            if (it.category?.toLowerCase(Locale.ENGLISH).equals(category))
                filteredInformations?.add(it)
        }

        activity?.runOnUiThread {
            informationRecyclerAdapter = InformationRecyclerAdapter(
                context!!,
                filteredInformations!!,
                (activity as DashboardActivity).supportFragmentManager
            )
            recycler_information_list.adapter = informationRecyclerAdapter
        }
    }
}