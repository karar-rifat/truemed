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
import com.example.pillreminder.models.Vaccine
import com.example.pillreminder.receivers.AlarmReceiver
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.vaccine_add.*
import kotlinx.android.synthetic.main.vaccine_add.view.*
import java.text.SimpleDateFormat
import java.util.*

class VaccineActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Validator.ValidationListener {
    var dateTime: String? = ""
    val finalCal : Calendar? = Calendar.getInstance()
    private lateinit var flag : String

    @NotEmpty
    private var vNameET: EditText? = null
    @NotEmpty
    private var vPlaceET: EditText? = null
    @NotEmpty
    private var vMobileET: EditText? = null
    @NotEmpty
    private var vTimeET: EditText? = null
    @NotEmpty
    private var vNoteET: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vaccine_add)
        var view : View = this.findViewById(android.R.id.content)
        vNameET = view.editText24
        vPlaceET = view.editText25
        vMobileET = view.editText28
        vTimeET = view.etTimeVacAdd
        vNoteET = view.editText29

        val validator = Validator(this)
        validator.setValidationListener(this)

        etTimeVacAdd.setOnClickListener {
            callDatePicker("vaccine")
            setTime("time")
        }

        button15.setOnClickListener {
            //            Toast.makeText(context, switchReminder.isChecked.toString(), Toast.LENGTH_LONG).show()
            validator.validate()
        }
        imageView8.setOnClickListener {
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

        val task_id = System.currentTimeMillis()
        Thread{
            val user_id= SharedPrefHelper(this).getInstance().getUserId()
            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newVaccine = Vaccine(editText24.text.toString(),editText25.text.toString(),editText28.text.toString(),etTimeVacAdd.text.toString(),swRemAddVac.isChecked,editText29.text.toString(),task_id,user_id!!,currentDateTime,false)
            var id = database.vaccineDao().saveVaccine(newVaccine)
            newVaccine.localId = id.toInt()
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            runOnUiThread {
                //  (activity as DashboardActivity).startAlarm(finalCal!!,"once")
                startAlarm(finalCal!!,"once", id.toInt(), "vaccine", editText24.text.toString(), etTimeVacAdd.toString(),task_id)
            }
        }.start()
        Toast.makeText(this, "Vaccine Created Successfully", Toast.LENGTH_LONG).show();
        finish()
    }
    private fun setTime(type : String){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            val format = SimpleDateFormat("HH:mm")
            if(type == "time"){
                if(dateTime==""){
                    Toast.makeText(this, "Select date first!", Toast.LENGTH_LONG).show();
                }else {
                    finalCal?.set(Calendar.HOUR_OF_DAY, hour)
                    finalCal?.set(Calendar.MINUTE, minute)
                    finalCal?.set(Calendar.SECOND, 0)
                    etTimeVacAdd.setText(dateTime + "  " + format.format(cal.time))
                }
            }else if(type == "timeIn"){
                etTimeVacAdd.setText(format.format(cal.time))
            }
//            (activity as DashboardActivity).startAlarm(cal)
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    fun startAlarm(c: Calendar, type: String, id: Int, reminderType: String, title: String, time: String, task_id:Long) {

        Log.e("startAlam",c.time.toString())
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
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

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
            dateTime = current
            finalCal?.set(Calendar.YEAR, year)
            finalCal?.set(Calendar.MONTH, month)
            finalCal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        }else{
            dateTime = current

        }
    }
}