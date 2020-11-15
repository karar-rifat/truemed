package com.example.pillreminder.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.TestAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.TimePivot
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_dashboard_2.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FragmentDashboard : Fragment(){
    lateinit var morningAdapter: TestAdapter
    lateinit var noonAdapter: TestAdapter
    lateinit var eveningAdapter: TestAdapter
    lateinit var nightAdapter: TestAdapter

    var pills: List<Pill>?=null
    var appointments: List<TimePivot>?=null
    var vaccines: List<TimePivot>?=null
    var simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    var dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
    var timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    var dayOfWeek:String?=null

    var dataMorning = ArrayList<String>()
    var dataNoon = ArrayList<String>()
    var dataEvening = ArrayList<String>()
    var dataNight = ArrayList<String>()

    var c = Calendar.getInstance()
//    var current = DateFormat.getDateInstance().format(c.time)
    var current = simpleDateFormat.format(c.time).toString()

    val user_id:String?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard_2,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        if (Helper.isAppRunning(context!!, "com.example.pillreminder")) {
//            Log.e("Helper","app is running")
//        } else {
//            // App is not running
//            Log.e("Helper","app is not running")
//        }
//        Ringtone.runRingtone(context!!)


        val todayDate = simpleDateFormat.format(Date()).toString()
        dayOfWeek = dayOfWeekFormat.format(c.time).toString()
        val time=timeFormat.format(Date()).toString()
        Log.e("check date dashboard: ", todayDate)

        dashboardDate.text = current
//        dashboardTime.text=time

        dashboardDay.text = dayOfWeek

        arrowLeft.setOnClickListener {
            val daysToDecrement = -1
            c.add(Calendar.DATE, daysToDecrement)
//            current = DateFormat.getDateInstance().format(c.time)
            current = simpleDateFormat.format(c.time).toString()
            dayOfWeek = dayOfWeekFormat.format(c.time).toString()
//            setData(true)
            setPillList()
            dashboardDate.text=current

            dashboardDay.text = dayOfWeek

        }
        arrowRight.setOnClickListener {
//            Ringtone.stopRingtone()
            val daysToIncrement = 1
            c.add(Calendar.DATE, daysToIncrement)
//            current = DateFormat.getDateInstance().format(c.time)
            current = simpleDateFormat.format(c.time).toString()
            dayOfWeek = dayOfWeekFormat.format(c.time).toString()
//            setData(true)
            setPillList()
            dashboardDate.text=current

            dashboardDay.text = dayOfWeek

        }

        recycler_morning.setHasFixedSize(true)
        recycler_morning.layoutManager = GridLayoutManager(context,4)
        recycler_noon.setHasFixedSize(true)
        recycler_noon.layoutManager = GridLayoutManager(context,4)
        recycler_evening.setHasFixedSize(true)
        recycler_evening.layoutManager = GridLayoutManager(context,4)
        recycler_night.setHasFixedSize(true)
        recycler_night.layoutManager = GridLayoutManager(context,4)

        morningAdapter = TestAdapter(context!!, dataMorning,"morning",::itemClick)
        noonAdapter = TestAdapter(context!!, dataNoon,"noon",::itemClick)
        eveningAdapter = TestAdapter(context!!, dataEvening,"evening",::itemClick)
        nightAdapter = TestAdapter(context!!, dataNight,"night",::itemClick)
        recycler_morning.adapter = morningAdapter
        recycler_noon.adapter = noonAdapter
        recycler_evening.adapter = eveningAdapter
        recycler_night.adapter = nightAdapter


        setPillList()
//        setData(false)

        setGridClicks()
//        setRecyclerClicks()

//        Thread{
//            isDataFetched()
//        }.start()

    }

    override fun onResume() {
        super.onResume()

        // Register Receiver to receive messages.
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("fetch-data"))

    }

    // handler for received Intents for the "my-event" event
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Extract data included in the Intent
            val isDataFetched = intent.getBooleanExtra("isDataFetched",false)
            if (isDataFetched){
                setPillList()
            }
            Log.d("receiver", "Got message: $isDataFetched")
        }
    }

    fun isDataFetched(){
        if (Helper.isDataFetched)
            setPillList()
    }

     fun setPillList() {
        dataMorning.clear()
        dataNoon.clear()
        dataEvening.clear()
        dataNight.clear()
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        val t1 = Thread {
            pills = database.pillDao().userPills(Helper.user_id!!)
            appointments = database.timePivotDao().getUserData(Helper.user_id!!,"appointment","%$current%")
            vaccines = database.timePivotDao().getUserData(Helper.user_id!!,"vaccine","%$current%")
            Log.e("setPillList", "entered")
            pills?.forEach{
                Log.e("setPillList", """${it.localId}  ${it.title}""")
            }
            if(pills == null){
                pills= arrayListOf()
            }else{

                pills?.forEach {

                    //check periodically and multiply with frequency for end date
                    if (!it.everyday!!) {
                        it.noOfDay = it.noOfDay!! * (it.frequency!!+1)
                    }
                    val c = Calendar.getInstance()
                    c.setTime(simpleDateFormat.parse(it.startFrom))
                    val createdTimeMillis=c.timeInMillis
                    c.add(Calendar.DATE, it.noOfDay!!.toInt())

                    val dateToEnd: Date = simpleDateFormat.parse(simpleDateFormat.format(c.time))
                    val currentDate: Date = simpleDateFormat.parse(current)
                    val createdAt: Date = simpleDateFormat.parse(it.startFrom)

                    c.setTime(simpleDateFormat.parse(current))
                    val currentTimeMillis=c.timeInMillis

                    Log.e("check date dashboard ", dateToEnd.toString() + " " + current+" "+it.startFrom)

                    //check if the selected date has any reminder date or not
                    if ((currentDate.compareTo(dateToEnd) < 0&&currentDate.compareTo(createdAt) > 0)||currentDate.compareTo(createdAt) == 0) {
                      //check date difference and compare frequency to show pill depending on frequency
                        val difference: Long = currentTimeMillis - createdTimeMillis
                        val dateDifference = difference / (24 * 60 * 60 * 1000)
                        Log.e("dashboard", "setPillList: date difference $dateDifference $difference")
                        if (dateDifference.toInt() % (if (it.everyday!!) it.frequency!! else (it.frequency!!+1)) == 0) {
                            try {
                                if (it.morning!!.isNotEmpty())
                                    dataMorning.add("pill")
                                if (it.noon!!.isNotEmpty())
                                    dataNoon.add("pill")
                                if (it.evening!!.isNotEmpty())
                                    dataEvening.add("pill")
                                if (it.night!!.isNotEmpty())
                                    dataNight.add("pill")
                            }catch (ex:Exception){}
                        }
                    }
                }
            }

            if(appointments == null){
                appointments= arrayListOf()
            }else{
                appointments?.forEach {
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if(hour in 6..11){
                        dataMorning.add("appointment")
                    }else if(hour>=12 && hour<16){
                        dataNoon.add("appointment")
                    }else if(hour>=16 && hour<20){
                        dataEvening.add("appointment")
                    }else if(hour>=20 || hour<6){
                        dataNight.add("appointment")
                    }
                    //Log.e("temp1",hour.toString())
                }
            }
            if(vaccines == null){
                vaccines= arrayListOf()
            }else{
                vaccines?.forEach {
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if(hour in 6..11){
                        dataMorning.add("vaccine")
                    }else if(hour>=12 && hour<16){
                        dataNoon.add("vaccine")
                    }else if(hour>=16 && hour<20){
                        dataEvening.add("vaccine")
                    }else if(hour>=20 || hour<6){
                        dataNight.add("vaccine")
                    }
                    //Log.e("temp1",hour.toString())
                }
            }

            activity?.runOnUiThread {
                morningAdapter.notifyDataSetChanged()
                noonAdapter.notifyDataSetChanged()
                eveningAdapter.notifyDataSetChanged()
                nightAdapter.notifyDataSetChanged()
            }
            database.close()
        }
        t1.start()
    }

    fun refreshData(){
        (activity as DashboardActivity).runOnUiThread {
            morningAdapter.notifyDataSetChanged()
            noonAdapter.notifyDataSetChanged()
            eveningAdapter.notifyDataSetChanged()
            nightAdapter.notifyDataSetChanged()
        }
    }

    private fun setData(flag : Boolean) {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Thread{

//            pills = database.pillDao().pills()
//            pills.forEach{
//                Log.e("setData", """${it.id}  ${it.title}""")
//            }
            dataMorning.clear()
            dataNoon.clear()
            dataEvening.clear()
            dataNight.clear()
//            appointments = database.appointmentDao().dateWiseAppointments("%$current%")
//            vaccines = database.vaccineDao().dateWiseVaccines("%$current%")
            appointments = database.timePivotDao().getUserData((activity as DashboardActivity).user_id!!,"appointment","%$current%")
            vaccines = database.timePivotDao().getUserData((activity as DashboardActivity).user_id!!,"vaccine","%$current%")
//            appointments.forEach{
//                Log.e("appointments dashboard",it.title)
//            }
            if(flag){
                if(pills == null){
                    pills= arrayListOf()
                }else{
                    pills?.forEach {
                        val startDate : Date = simpleDateFormat.parse(it.startFrom)
                        var endDate : Date = simpleDateFormat.parse(current)
//                        var diff = date1.ge
                        val diff = getDifferenceInDays(startDate,endDate)
                        Log.e("difference",diff.toString())
                        if(diff<=it.noOfDay!!-1 && diff>=0){
                            if(it.morning!!.length>0)
                                dataMorning.add("pill")
                            if(it.noon!!.length>0)
                                dataNoon.add("pill")
                            if(it.evening!!.length>0)
                                dataEvening.add("pill")
                            if(it.night!!.length>0)
                                dataNight.add("pill")
                        }
                    }
                }
            }
            if(appointments == null){
                appointments= arrayListOf()
            }else{
                appointments?.forEach {
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if(hour in 6..11){
                        dataMorning.add("appointment")
                    }else if(hour>=12 && hour<16){
                        dataNoon.add("appointment")
                    }else if(hour>=16 && hour<20){
                        dataEvening.add("appointment")
                    }else if(hour>=20 || hour<6){
                        dataNight.add("appointment")
                    }
                    //Log.e("temp1",hour.toString())
                }
            }
            if(vaccines == null){
                vaccines= arrayListOf()
            }else{
                vaccines?.forEach {
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if(hour in 6..11){
                        dataMorning.add("vaccine")
                    }else if(hour>=12 && hour<16){
                        dataNoon.add("vaccine")
                    }else if(hour>=16 && hour<20){
                        dataEvening.add("vaccine")
                    }else if(hour>=20 || hour<6){
                        dataNight.add("vaccine")
                    }
                    //Log.e("temp1",hour.toString())
                }
            }

            (activity as DashboardActivity).runOnUiThread {
                morningAdapter.notifyDataSetChanged()
                noonAdapter.notifyDataSetChanged()
                eveningAdapter.notifyDataSetChanged()
                nightAdapter.notifyDataSetChanged()
                database.close()
            }
        }.start()

    }

    private fun getDifferenceInDays(startDate:Date,endDate:Date) : Int{
        val difference = endDate.time - startDate.time
        return (difference/(1000*60*60*24)).toInt()
    }

    private fun setRecyclerClicks() {
        recycler_morning.setOnClickListener {
            Log.e("morning recycler","clicked")
            (activity as DashboardActivity).showPillList("morning",current,dayOfWeek!!)
        }
        recycler_noon.setOnClickListener {
            (activity as DashboardActivity).showPillList("noon",current,dayOfWeek!!)

        }
        recycler_evening.setOnClickListener {
            (activity as DashboardActivity).showPillList("evening",current,dayOfWeek!!)

        }
        recycler_night.setOnClickListener {
            (activity as DashboardActivity).showPillList("night",current,dayOfWeek!!)
        }
    }

    private fun setGridClicks() {
        morningGrid.setOnClickListener {
            (activity as DashboardActivity).showPillList("morning",current,dayOfWeek!!)
        }
        afternoonGrid.setOnClickListener {
            (activity as DashboardActivity).showPillList("noon",current,dayOfWeek!!)
        }
        eveningGrid.setOnClickListener {
            (activity as DashboardActivity).showPillList("evening",current,dayOfWeek!!)

        }
        nightGrid.setOnClickListener {
            (activity as DashboardActivity).showPillList("night",current,dayOfWeek!!)
        }


        recycler_morning.setOnClickListener {
            (activity as DashboardActivity).showPillList("morning",current,dayOfWeek!!)
        }
        recycler_noon.setOnClickListener {
            (activity as DashboardActivity).showPillList("noon",current,dayOfWeek!!)
        }
        recycler_evening.setOnClickListener {
            (activity as DashboardActivity).showPillList("evening",current,dayOfWeek!!)
        }
        recycler_night.setOnClickListener {
            (activity as DashboardActivity).showPillList("night",current,dayOfWeek!!)
        }
    }

    fun itemClick(code:String){
        Log.e("recycler","item clicked $code")

        if (code=="morning"){
            (activity as DashboardActivity).showPillList("morning",current,dayOfWeek!!)
        }
        if (code=="noon"){
            (activity as DashboardActivity).showPillList("noon",current,dayOfWeek!!)
        }
        if (code=="evening"){
            (activity as DashboardActivity).showPillList("evening",current,dayOfWeek!!)
        }
        if (code=="night"){
            (activity as DashboardActivity).showPillList("night",current,dayOfWeek!!)
        }
    }
}

