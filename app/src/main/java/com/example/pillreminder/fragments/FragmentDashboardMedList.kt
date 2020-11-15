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
import com.example.pillreminder.adaptors.DashboardMedRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.DashboardData
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Vaccine
import kotlinx.android.synthetic.main.fragment_dashboard_med_list.*
import kotlinx.android.synthetic.main.fragment_dashboard_med_list.arrowLeft
import kotlinx.android.synthetic.main.fragment_dashboard_med_list.arrowRight
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentDashboardMedList : Fragment() {
    lateinit var code: String
    lateinit var date: String
    var pills: List<Pill>? = null
    lateinit var appointments: List<Appointment>
    lateinit var vaccines: List<Vaccine>

    var dashData: ArrayList<DashboardData>? = ArrayList()
    var temp: DashboardData? = null

    var c = Calendar.getInstance()

    //    var current = DateFormat.getDateInstance().format(c.time)!!
    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    var currentDate = simpleDateFormat.format(c.time).toString()
//    val current = FragmentDashboard().current
    var dayOfWeek:String?=null

    lateinit var dashboardMedRecyclerAdapter: DashboardMedRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard_med_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //code = arguments!!.getString("code")
        //Log.e("onAttach",code)
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause", "I am Clicked")
        (activity as DashboardActivity).supportFragmentManager.beginTransaction()
            .remove((activity as DashboardActivity).activeFragment)
        (activity as DashboardActivity).showProPic()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        code = arguments!!.getString("code")!!
        date = arguments!!.getString("date")!!
        dayOfWeek = arguments!!.getString("day")!!

        if (code=="morning"){
            iconTime.visibility=View.VISIBLE
            iconTime.setImageResource(R.drawable.morning_icon)
        }
        if (code=="noon"){
            iconTime.visibility=View.VISIBLE
            iconTime.setImageResource(R.drawable.noon_icon)
        }
        if (code=="evening"){
            iconTime.visibility=View.VISIBLE
            iconTime.setImageResource(R.drawable.evening_icon)
        }
        if (code=="night"){
            iconTime.visibility=View.VISIBLE
            iconTime.setImageResource(R.drawable.night_icon)
        }

        currentDate=date
        val todayDate: Date = simpleDateFormat.parse(currentDate)
        c.setTime(todayDate)
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        tvDay.text = dayOfWeek
        Log.e("onViewCreated", code+" "+date)

        val sdf = SimpleDateFormat("hh:mm a")
        val currentTime = sdf.format(Date())

        tvDate.text = currentDate
//        tvTime.text = currentTime

        getData()

        arrowLeft.setOnClickListener {
            val daysToDecrement = -1
            c.add(Calendar.DATE, daysToDecrement)
            currentDate = simpleDateFormat.format(c.time).toString()
            dayOfWeek = dayOfWeekFormat.format(c.time).toString()
            tvDate.text=currentDate
            tvDay.text = dayOfWeek
            getData()
        }
        arrowRight.setOnClickListener {
            val daysToIncrement = 1
            c.add(Calendar.DATE, daysToIncrement)
            currentDate = simpleDateFormat.format(c.time).toString()
            dayOfWeek = dayOfWeekFormat.format(c.time).toString()
            tvDate.text=currentDate
            tvDay.text = dayOfWeek

            getData()
        }

        recyclerDashMed.setHasFixedSize(true)
        recyclerDashMed.layoutManager = LinearLayoutManager(context)
    }

    fun getData(){
        Thread {
            val database = Room.databaseBuilder(
                context!!,
                AppDb::class.java!!,
                "pill_reminder"
            ).fallbackToDestructiveMigration().build()

            dashData?.clear()
            temp=null

//            pills = arrayListOf()
//            appointments= arrayListOf()
//            vaccines= arrayListOf()

//            (pills as ArrayList).clear()
//            (appointments as ArrayList).clear()
//            (vaccines as ArrayList).clear()

//            appointments = database.appointmentDao().dateWiseAppointments("%$current%")
            appointments = database.appointmentDao().dateWiseAppointments("appointment","%$currentDate%")
            Log.e("dashboard list: ", "appointment size-${currentDate + " " + appointments.size}")
//            vaccines = database.vaccineDao().dateWiseVaccines("%$current%")
            vaccines = database.vaccineDao().dateWiseVaccines("vaccine","%$currentDate%")
            Log.e("dashboard list: ", "vaccine size-${currentDate + " " + vaccines.size}")

            if (code == "morning") {
                Log.e("onViewCreated morning", "entered")
                pills = database.pillDao().morningPills()
            } else if (code == "noon") {
                pills = database.pillDao().noonPills()

            } else if (code == "evening") {
                pills = database.pillDao().eveningPills()

            } else if (code == "night") {
                pills = database.pillDao().nightPills()
            }
            if (pills == null) {
                pills = arrayListOf()
            }
            pills!!.forEach {
                val c=Calendar.getInstance()
                c.setTime(simpleDateFormat.parse(it.startFrom))
                val createdTimeMillis=c.timeInMillis
                c.add(Calendar.DATE, it.noOfDay!!.toInt())

                val dateToEnd:Date = simpleDateFormat.parse(simpleDateFormat.format(c.time))
                val currentDate:Date=simpleDateFormat.parse(currentDate)
                val createdAt: Date = simpleDateFormat.parse(it.startFrom)

                c.setTime(currentDate)
                val currentTimeMillis=c.timeInMillis
                Log.e("check date List ", dateToEnd.toString() + " " + currentDate+" "+it.startFrom)

                //check if the selected date has any reminder date or not
                if ((currentDate.compareTo(dateToEnd) < 0&&currentDate.compareTo(createdAt) > 0)||currentDate.compareTo(createdAt) == 0) {

                    //check date difference and compare frequency to show pill depending on frequency
                    val difference: Long = currentTimeMillis - createdTimeMillis
                    val dateDifference = difference / (24 * 60 * 60 * 1000)
                    Log.e("dashboard", "setPillList: date difference $dateDifference $difference")
                    if (dateDifference.toInt() % (if (it.everyday!!) it.frequency!! else (it.frequency!!+1)) == 0) {

                        if (code == "morning") {
                            temp = DashboardData(it.localId, it.title!!,"pill", code, it.morning!!,it.beforeOrAfterMeal!!,it.noOfDose!!,it.stock!!)
                        } else if (code == "noon") {
                            temp = DashboardData(it.localId, it.title!!,"pill", code, it.noon!!,it.beforeOrAfterMeal!!,it.noOfDose!!,it.stock!!)
                        } else if (code == "evening") {
                            //Log.i("pills inside evening",""" ID - ${it.id}""")
                            //dashData?.add(DashboardData(it.id,it.title!!,code,it.evening!!))
                            temp = DashboardData(it.localId, it.title!!,"pill", code, it.evening!!,it.beforeOrAfterMeal!!,it.noOfDose!!,it.stock!!)
                        } else if (code == "night") {
                            temp = DashboardData(it.localId, it.title!!,"pill", code, it.night!!,it.beforeOrAfterMeal!!,it.noOfDose!!,it.stock!!)
                        }
                        Log.i("pills", """ ID - ${it.localId}""")
                        Log.i("temp", """ TITLE - ${temp?.title}""")
                    }

                    if (temp!=null)
                        dashData?.add(temp!!)
                    System.out.println(dashData)

                }
            }

            if (appointments.isEmpty()) {
                appointments = arrayListOf()
            } else {
                appointments.forEach {
                    var temp: DashboardData? = null
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if (code == "morning" && hour in 6..11) {
                        temp = DashboardData(it.localId, it.title!!, "appointment", temp1[1])
                    } else if (code == "noon" && hour >= 12 && hour < 16) {
                        temp = DashboardData(it.localId, it.title!!, "appointment", temp1[1])
                    } else if (code == "evening" && hour >= 16 && hour < 20) {
                        temp = DashboardData(it.localId, it.title!!, "appointment", temp1[1])
                    } else if (code == "night" && hour >= 20 || hour < 6) {
                        temp = DashboardData(it.localId, it.title!!, "appointment", temp1[1])
                    }
                    temp?.let { it1 -> dashData?.add(it1) }
//                    Log.e("temp", temp?.time)
                }
            }
            if (vaccines.isEmpty()) {
                vaccines = arrayListOf()
            } else {
                Log.e("vaccine list: ", "onViewCreated: ${vaccines.size}")
                vaccines.forEach {
                    var temp: DashboardData? = null
                    var temp1 = it.time?.split("  ")
                    var hour = temp1!![1]!!.split(":")!![0]!!.toInt()
                    if (code == "morning" && hour in 6..11) {
                        temp = DashboardData(it.localId, it.title!!, "vaccine", temp1[1])
                    } else if (code == "noon" && hour >= 12 && hour < 16) {
                        temp = DashboardData(it.localId, it.title!!, "vaccine", temp1[1])
                    } else if (code == "evening" && hour >= 16 && hour < 20) {
                        temp = DashboardData(it.localId, it.title!!, "vaccine", temp1[1])
                    } else if (code == "night" && hour >= 20 || hour < 6) {
                        temp = DashboardData(it.localId, it.title!!, "vaccine", temp1[1])
                    }
                    temp?.let { it1 -> dashData?.add(it1) }
                    //Log.e("temp1",hour.toString())
                }
            }
            if (dashData == null) {
                dashData = arrayListOf()
            }
            (activity as DashboardActivity).runOnUiThread {
                dashboardMedRecyclerAdapter = DashboardMedRecyclerAdapter(
                    context!!,
                    dashData!!,
                    (activity as DashboardActivity).supportFragmentManager
                )
                recyclerDashMed.adapter = dashboardMedRecyclerAdapter
                if (dashData?.size==0){
                    iconTime.visibility=View.INVISIBLE
                }
                database.close()
            }
        }.start()
    }
}