package com.example.pillreminder.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.pillreminder.R
import kotlinx.android.synthetic.main.fragment_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context.ALARM_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.receivers.AlarmReceiver






class FragmentReminder : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnAlarm.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                txtAlarm.text = SimpleDateFormat("HH:mm").format(cal.time)
            //s    (activity as DashboardActivity).startAlarm(cal,"once")
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

//    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
//        val c = Calendar.getInstance()
//        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
//        c.set(Calendar.MINUTE, minute)
//        c.set(Calendar.SECOND, 0)
//        Log.e("setting time","entered")
//        Log.e("setting time",hourOfDay.toString())
//        Log.e("setting time",minute.toString())
//        startAlarm(c)
//    }

//    private fun startAlarm(c: Calendar) {
//        Log.e("startAlam","entered")
//        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
//
//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1)
//        }
//
//        alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
//    }
//    companion object {
//        fun newInstance(): FragmentReminder = FragmentReminder()
//    }
}