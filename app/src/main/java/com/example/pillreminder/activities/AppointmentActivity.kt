package com.example.pillreminder.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.fragments.DatePickerFragment
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.receivers.AlarmReceiver
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.appointment_add.*
import kotlinx.android.synthetic.main.appointment_add.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AppointmentActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Validator.ValidationListener  {
    var dateTime: String? = ""
    private lateinit var flag : String
    val finalCal : Calendar? = Calendar.getInstance()

    @NotEmpty
    private var appNameET: EditText? = null
    @NotEmpty
    private var appPlaceET: EditText? = null
    @NotEmpty
    private var appMobileET: EditText? = null
    @NotEmpty
    private var appTimeET: EditText? = null
    @NotEmpty
    private var appNoteET: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appointment_add)
        var view : View= this.findViewById(android.R.id.content)
        appNameET = view.editText
        appPlaceET = view.editText2
        appMobileET = view.editText3
        appTimeET = view.etAppoTimeAdd
        appNoteET = view.editText7

        val validator = Validator(this)
        validator.setValidationListener(this)

        etAppoTimeAdd.setOnClickListener {
            callDatePicker("appointment")
            setTime("time")
        }

        button2.setOnClickListener {
            validator.validate()
        }

        imageButton13.setOnClickListener {
            //activity destroy
            finish()
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        for (error in errors) {
            val view = error.getView()
            val message = error.getCollatedErrorMessage(this)

            // Display error messages ;)
            if (view is EditText) {
                (view as EditText).error = message
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
//        Log.i("appointment","""${finalCal.timeInMillis}""")

        val task_id = System.currentTimeMillis()
        Thread{
            val user_id= SharedPrefHelper(this).getInstance().getUserId()

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newAppo = Appointment(editText.text.toString(),editText2.text.toString(),editText3.text.toString(),etAppoTimeAdd.text.toString(),swRemAddApp.isChecked,editText7.text.toString(),task_id,user_id!!,currentDateTime,false)

            val id = database.appointmentDao().saveAppointment(newAppo).toInt()
            newAppo.localId = id
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            runOnUiThread {
                //   (activity as DashboardActivity).startAlarm(finalCal!!,"once")
                startAlarm(finalCal!!,"once", id, "appointment", editText.text.toString(), etAppoTimeAdd.toString(),task_id)
//                        Log.i("appointment","""${finalCal?.timeInMillis}""")
                //(DashboardActivity.fragmentAppointmentList.appointments as ArrayList).add(newAppo)
                //s(activity as DashboardActivity).fragmentAppointmentList.appointmentRecyclerAdapter.notifyDataSetChanged()
            }
        }.start()
        Toast.makeText(this, "Appointment Created Successfully", Toast.LENGTH_LONG).show();
        finish()
    }

    private fun setTime(type : String){
//        val date = editText4.text.toString()
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)

            val format = SimpleDateFormat("HH:mm")
            val format2 = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
            if(type == "time"){
                if(dateTime==""){
                    Toast.makeText(this, "Select date first!", Toast.LENGTH_LONG).show();
                }else {
                    finalCal?.set(Calendar.HOUR_OF_DAY, hour)
                    finalCal?.set(Calendar.MINUTE, minute)
                    finalCal?.set(Calendar.SECOND, 0)
                    etAppoTimeAdd.setText(dateTime + "  " + format.format(cal.time))
                    Log.i("appointment","""${cal.timeInMillis} -- ${DateFormat.getDateInstance().format(finalCal!!.getTime())} -- ${format2.format(finalCal.time)}""")
                }
            }else if(type == "timeIn"){
                etAppoTimeAdd.setText(format.format(cal.time))
            }
//            (activity as DashboardActivity).startAlarm(cal)
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    fun startAlarm(c: Calendar, type: String, id: Int, reminderType: String, title: String, time: String, task_id:Long) {

        val format2 = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        Log.e("startAlam TOSTRING",c.time.toString())
        Log.e("startAlam FORMAT",format2.format(c.time))
        Log.e("startAlam TITLE",title)
        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", reminderType)
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id.toInt(), intent, 0)

//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1)
//        }
        if(type == "repeating"){
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

//            if (Build.VERSION.SDK_INT >= 23) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
//                    c.timeInMillis, pendingIntent);
//            } else if (Build.VERSION.SDK_INT >= 19) {
//                alarmManager.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent);
//            } else {
//                alarmManager.set(AlarmManager.RTC, c.timeInMillis, pendingIntent);
//            }
        }else{
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC,
                    c.timeInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC, c.timeInMillis, pendingIntent);
            }
        }

//        alarmManager!!.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent)
    }

    fun callDatePicker(type : String){
        flag = type
        val datePicker = DatePickerFragment()
        datePicker.show(supportFragmentManager, "date picker")
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR,year)
        c.set(Calendar.MONTH,month)
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//        val name: String =  registerName.text.toString()
//        Log.e("date set",name)

//        var current = DateFormat.getDateInstance().format(c.getTime())
        val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val current=simpleDateFormat.format(c.time).toString()
//        var date = findViewById<EditText>(R.id.registerDob)
//        return current
        if(flag=="appointment"){
//            editText4.text = Editable.Factory.getInstance().newEditable(current)
            dateTime = current
            finalCal?.set(Calendar.YEAR, year)
            finalCal?.set(Calendar.MONTH, month)
            finalCal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        }else{
            dateTime = current

        }
    }
}
