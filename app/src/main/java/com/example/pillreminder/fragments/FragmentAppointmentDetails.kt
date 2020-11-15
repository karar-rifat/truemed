package com.example.pillreminder.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.VaccineTimeRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Validate
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.service.DataDeleteService
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.appointment_add.*
import kotlinx.android.synthetic.main.fragment_appointment_details.*
import kotlinx.android.synthetic.main.fragment_appointment_details.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class FragmentAppointmentDetails : DialogFragment(), Validator.ValidationListener {
    var dateTime: String? = ""
    private var position: Int? = null
    private var id: Int? = null
    private var title: String? = null
    private var place: String? = null
    private var mobile: String? = null
    private var time: String? = null
    private var reminder: Boolean? = null
    private var note: String? = null
    private var task_id: Long? = null
    private var user_id: String? = null
    private var remoteId: Int? = null
    var times: MutableList<String> = mutableListOf<String>()
    var newTask_ids: MutableList<Long> = mutableListOf<Long>()
    var oldTask_ids: MutableList<Long> = mutableListOf<Long>()
    lateinit var vacTimeRecyclerAdapter: VaccineTimeRecyclerAdapter

    @NotEmpty
    private var appNameET: EditText? = null

    @NotEmpty
    private var appPlaceET: EditText? = null

    @NotEmpty
    private var appMobileET: EditText? = null

    //    @NotEmpty
    private var appTimeET: EditText? = null

//    @NotEmpty
    private var appNoteET: EditText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        id = arguments?.getInt("id")
        title = arguments?.getString("title")
        place = arguments?.getString("place")
        mobile = arguments?.getString("mobile")
        time = arguments?.getString("time")
        reminder = arguments?.getBoolean("reminder")
        note = arguments?.getString("note")
        task_id = arguments?.getLong("task_id")
        user_id = arguments?.getString("user_id")
        remoteId = arguments?.getInt("remoteId",0)

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread {
            val data = database.timePivotDao().getData(title!!, "appointment")
//            val all = database.timePivotDao().times()
            data.forEach {
                times.add(it.time!!)
                oldTask_ids.add(it.taskId!!)
                println(it.idToMatch)
                println(it.type)
            }
        }.start()
    }

    fun delete(position: Int) {
        Log.e("del clicked", times[position])
        times.removeAt(position)
        vacTimeRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_appointment_details, container)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Log.e("id from dialog", id.toString())

        appNameET = view.editText5
        appPlaceET = view.editText6
        appMobileET = view.editText21
        appTimeET = view.etTimeAppDetails
        appNoteET = view.editText23

        editText5.setText(title)
        editText6.setText(place)
        editText21.setText(mobile)
//        etTimeAppDetails.setText(time)
        swRemUpdateApp.isChecked = reminder!!
        editText23.setText(note)
        closeButton.setOnClickListener {
            dialog?.dismiss()
        }

        swRemUpdateApp.setOnCheckedChangeListener { buttonView, isChecked ->
            reminder = isChecked
        }

        val validator = Validator(this)
        validator.setValidationListener(this)

        etTimeAppDetails.setOnClickListener {
            (activity as DashboardActivity).callDatePicker("appDetails")
            setTime("time")
        }

        button4.setOnClickListener {
            validator.validate()
        }

        btnDuplicate.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to duplicate this appointment ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    duplicate()
                } )
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Duplicate Appointment")
            // show alert dialog
            alert.show()
            //duplicateAppointment()
        }

        imageButton10.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this appointment ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    deleteAppointment()
                } )
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete Appointment")
            // show alert dialog
            alert.show()
            //deleteAppointment()
        }

        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDate: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))
        imageButtonP4.setOnClickListener {
            if (etTimeAppDetails.text.toString().equals("")) {
                Toast.makeText(context, "Select date & time first!", Toast.LENGTH_LONG).show();
            } else {
                //compare selected time with current time
                val selectedDate: Date = simpleDateFormat.parse(time)
                if (selectedDate.compareTo(currentDate) > 0) {
                    times.add(time!!)
                    etTimeAppDetails.setText("")
                } else
                    Toast.makeText(context, "Previous time can't be selected!", Toast.LENGTH_SHORT)
                        .show()
            }
            vacTimeRecyclerAdapter.notifyDataSetChanged()
        }

        recyclerAppTime2.setHasFixedSize(true)
        recyclerAppTime2.layoutManager = LinearLayoutManager(context)
        vacTimeRecyclerAdapter = VaccineTimeRecyclerAdapter(context!!, times!!, ::delete)
        recyclerAppTime2.adapter = vacTimeRecyclerAdapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(getActivity());
        root.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        );

        // creating the fullscreen dialog
        val dialog = Dialog(getActivity()!!, R.style.MyCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE));
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
        // return  AlertDialog.Builder( (activity as DashboardActivity), R.style.FullScreenDialogStyle ).setView(R.layout.fragment_add_pill_first).create()
//        val context = ContextThemeWrapper(activity, R.style.FullScreenDialogStyle)
//        val builder = AlertDialog.Builder(context)
//        return builder.create()
    }

    private fun deleteAppointment() {
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread {
            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newAppo = Appointment(
                id!!,
                remoteId!!,
                title!!,
                place!!,
                mobile!!,
                time!!,
                reminder!!,
                note!!,
                task_id!!,
                user_id!!,
                currentDateTime,
                false
            )

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("appointment delete ", "starting delete service appointment $remoteId")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "appointment")
                service.putExtra("title", title)
                service.putExtra("remoteId", remoteId!!)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"appointment")
                Log.e("appointment delete ", "alarm for sync appointment")
            }

            database.appointmentDao().delete(newAppo)
            //delete timePivot for appointment

//            val timePivot=database.timePivotDao().getData(title!!,time!!,"appointment")
//            timePivot.forEach {
//                database.timePivotDao().deleteRows(it.localId,"appointment")
//                Log.e("delete appointment","deleted timePivot row ${it.idToMatch}")
//            }

//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }

            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentAppointmentList.appointments as ArrayList).removeAt(
                    position!!
                )
                (activity as DashboardActivity).fragmentAppointmentList.appointmentRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Appointment Deleted!!", Toast.LENGTH_LONG).show();
        database.close()
    }

     private fun duplicate() {


        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        val custom = activity as DashboardActivity


        Thread{

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newAppo = Appointment("Copy of $title",place!!,mobile!!,time!!,false,note!!,task_id!!,custom.user_id!!,currentDateTime,false)

            val id = database.appointmentDao().saveAppointment(newAppo).toInt()
            newAppo.localId = id
            times.forEachIndexed { index, s ->
                val newTimePivot = TimePivot(id,(activity as DashboardActivity).user_id!!,"Copy of $title","appointment",s,oldTask_ids[index],currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
            }
            custom.runOnUiThread {

                //update data to server with background service
                if (Helper.isConnected(context!!)) {
                    val token=Helper.token
                    Log.e("add appointment", "starting push service appointment")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "appointment")
                    service.putExtra("token", token)
                    context!!.startService(service)
                }else{
                    AlarmHelper().startAlarmForSync(context!!,"appointment")
                    Log.e("add appointment ", "alarm for sync appointment")
                }

//                custom.startAlarm(finalCal!!,"once", id, "Appointment", editText.text.toString(), editText4.text.toString(),task_id)
                (custom.fragmentAppointmentList.appointments as java.util.ArrayList).add(newAppo)
                custom.fragmentAppointmentList.appointmentRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Appointment Duplicated Successfully", Toast.LENGTH_LONG).show();

    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        for (error in errors) {
            val view = error.getView()
            val message = error.getCollatedErrorMessage(context)

            // Display error messages ;)
            if (view is EditText) {
                (view as EditText).error = message
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() {

        if (Validate().isNumeric(editText5.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Title can't be numeric",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (Validate().isNumeric(editText6.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Place can't be numeric",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!Validate().validatePhoneNumber(editText21.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Invalid phone number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (Validate().isNumeric(editText23.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Note can't be numeric",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (times.size == 0||etTimeAppDetails.text.isNotEmpty()) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Please add time and click +",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        oldTask_ids.forEachIndexed { index, i ->
            Log.e("appointment details:", "cancelling alarm")
            (activity as DashboardActivity).cancelAlarm(oldTask_ids[index])
        }

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        times.forEachIndexed { index, it ->
            val randomInteger = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ThreadLocalRandom.current().nextInt(10000, 100000)
            } else {
                index * 1000
            }
            Log.e("random generator", randomInteger.toString())
            var task = System.currentTimeMillis()
            if (task < 0)
                task = task * (-1) + randomInteger
            else
                task += randomInteger

            newTask_ids.add(task)
        }
        Thread {

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newAppo = Appointment(
                id!!,
                remoteId!!,
                editText5.text.toString(),
                editText6.text.toString(),
                editText21.text.toString(),
                time!!,
                reminder!!,
                editText23.text.toString(),
                task_id!!,
                user_id!!,
                currentDateTime,
                false
            )
            database.appointmentDao().update(newAppo)

            database.timePivotDao().deleteRows(id!!, "appointment")
            var sdf = SimpleDateFormat("MMM dd,yyyy  HH:mm")

            times.forEachIndexed { index, s ->
                val newTimePivot = TimePivot(id!!, (activity as DashboardActivity).user_id!!,  editText5.text.toString(), "appointment", s, newTask_ids[index], currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)

                val cal: Calendar = Calendar.getInstance()
                val date: Date = sdf.parse(s)
                val currentDate: Date = sdf.parse(sdf.format(Date()))
                cal.setTime(date)
                Log.e("print date", "date $s")
                System.out.println(cal)

                //check if the date is previous before starting alarm
                if (date.compareTo(currentDate) > 0) {
                    if (reminder!!)
                        (activity as DashboardActivity).startAlarm(
                            cal!!,
                            "once",
                            id!!,
                            "appointment",
                            true,
                            0,
                            editText5.text.toString(),
                            s,
                            newTask_ids[index]
                        )
                }
            }

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("appointment time delete", "starting delete service appointment")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "timePivot")
                service.putExtra("title", title)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"appointment")
                Log.e("appointment delete ", "alarm for sync appointment")
            }

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                val token=Helper.token
                Log.e("details appointment", "starting push service appointment")
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "appointment")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"appointment")
                Log.e("details appointment ", "alarm for sync appointment")
            }

            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentAppointmentList.appointments as ArrayList).set(
                    position!!,
                    newAppo
                )
                (activity as DashboardActivity).fragmentAppointmentList.appointmentRecyclerAdapter?.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Appointment Updated Successfully", Toast.LENGTH_LONG).show();

    }

    private fun setTime(type: String) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            val sdf = SimpleDateFormat("HH:mm")
            if (type == "time") {
                if ((activity as DashboardActivity).temp == "") {
                    Toast.makeText(context, "Select date first!", Toast.LENGTH_LONG).show();
                } else {
                    time=(activity as DashboardActivity).temp + "  " + sdf.format(cal.time)
                    etTimeAppDetails.setText((activity as DashboardActivity).temp + "  " + formatTime(sdf.format(cal.time)))
                    (activity as DashboardActivity).temp = ""
                }
            } else if (type == "timeIn") {
                etTimeAppDetails.setText(sdf.format(cal.time))
            }
//            (activity as DashboardActivity).startAlarm(cal)
        }
//        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        var timePicker = TimePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        )
        timePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePicker.show()
    }

    //formate time with AM,PM for button
    fun formatTime(time:String): String? {
        val timeParts = time.split(":")
        var hour=timeParts[0].toInt()
        val minute=timeParts[1].toInt()
        val format: String
        val formattedTime: String
        when {
            hour == 0 -> {
                hour += 12
                format = "AM"
            }
            hour == 12 -> {
                format = "PM"
            }
            hour > 12 -> {
                hour -= 12
                format = "PM"
            }
            else -> {
                format = "AM"
            }
        }
        val newMinute = if (minute < 10) "0$minute" else minute.toString()
        formattedTime = "$hour:$newMinute $format"
        return formattedTime
    }
}